package megaminds.actioninventory.openers;

import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.serialization.wrappers.Validated;
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

@NoArgsConstructor
@PolyName("Entity")
public final class EntityOpener extends BasicOpener {
	private static final List<EntityOpener> OPENERS = new ArrayList<>();

	@Getter @Setter private String entitySelector;	
	@Getter @Setter private EntityPredicate entityPredicate;
	
	@Exclude private EntitySelector selector;

	public EntityOpener(Identifier guiName, String entitySelector) {
		super(guiName);
		this.entitySelector = entitySelector;
	}

	@Override
	public boolean open(ServerPlayerEntity player, Object... context) {
		Entity e = (Entity) context[0];
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
		
		String whole = "@s"+entitySelector.strip();
		
		try {
			this.selector = new EntitySelectorReader(new StringReader(whole)).read();
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException("Failed to read entity selector for an EntityOpener.", e);
		}
	}

	public static boolean tryOpen(ServerPlayerEntity p, Entity e) {
		return Helper.getFirst(OPENERS, o->o.open(p, e))!=null;
	}
	
	public static void registerCallbacks() {
		UseEntityCallback.EVENT.register((p,w,h,e,r)->
			!w.isClient&&tryOpen((ServerPlayerEntity)p, e) ? ActionResult.SUCCESS : ActionResult.PASS
		);
		AttackEntityCallback.EVENT.register((p,w,h,e,r)->
			!w.isClient&&tryOpen((ServerPlayerEntity)p, e) ? ActionResult.SUCCESS : ActionResult.PASS
		);
	}
	
	public static void clearOpeners() {
		OPENERS.clear();
	}
	
	@Override
	public void validate() {
		super.validate();
		validateSelector();
		Validated.validate(!OPENERS.contains(this) && OPENERS.add(this), "Failed to add Block opener to list.");
	}
}