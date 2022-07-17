package megaminds.actioninventory.openers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.Exclude;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

@PolyName("Entity")
public final class EntityOpener extends BasicOpener {
	private static final Identifier TYPE = new Identifier(ActionInventoryMod.MOD_ID, "entity");

	private String entitySelector;	
	private EntityPredicate entityPredicate;

	@Exclude private EntitySelector selector;

	public EntityOpener() {}
	
	public EntityOpener(Identifier guiName, String entitySelector, EntityPredicate entityPredicate) {
		super(guiName);
		this.entitySelector = entitySelector;
		this.entityPredicate = entityPredicate;
	}

	@Override
	public boolean open(ServerPlayerEntity player, Object... context) {
		var e = (Entity) context[0];
		if (selector==null || entityPredicate.test(player, e) && matches(e)) {
			return super.open(player, context);
		}
		return false;
	}

	private boolean matches(Entity e) {
		try {
			return e.equals(selector.getEntity(e.getCommandSource().withMaxLevel(2)));
		} catch (CommandSyntaxException e1) {
			return false;
		}
	}

	private void validateSelector() {
		if (entitySelector==null || entitySelector.isBlank()) return;

		var whole = "@s"+entitySelector.strip();

		try {
			this.selector = new EntitySelectorReader(new StringReader(whole)).read();
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException("Failed to read entity selector for an EntityOpener.", e);
		}
	}

	public static boolean tryOpen(ServerPlayerEntity p, Entity e) {
		return Helper.getFirst(ActionInventoryMod.OPENER_LOADER.getOpeners(TYPE), o->o.open(p, e))!=null;
	}

	public static void registerCallbacks() {
		UseEntityCallback.EVENT.register((p,w,h,e,r) -> !w.isClient&&tryOpen((ServerPlayerEntity)p, e) ? ActionResult.SUCCESS : ActionResult.PASS);
		AttackEntityCallback.EVENT.register((p,w,h,e,r) -> !w.isClient&&tryOpen((ServerPlayerEntity)p, e) ? ActionResult.SUCCESS : ActionResult.PASS);
	}

	@Override
	public void validate() {
		super.validate();
		validateSelector();
		if (entityPredicate==null) entityPredicate = EntityPredicate.ANY;
	}

	@Override
	public Identifier getType() {
		return TYPE;
	}

	public String getEntitySelector() {
		return entitySelector;
	}

	public void setEntitySelector(String entitySelector) {
		this.entitySelector = entitySelector;
	}

	public EntityPredicate getEntityPredicate() {
		return entityPredicate;
	}

	public void setEntityPredicate(EntityPredicate entityPredicate) {
		this.entityPredicate = entityPredicate;
	}
}