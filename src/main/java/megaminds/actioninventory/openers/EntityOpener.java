package megaminds.actioninventory.openers;

import java.util.ArrayList;
import java.util.List;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.misc.LevelSetter;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.Exclude;
import megaminds.actioninventory.util.annotations.TypeName;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

@TypeName("Entity")
public final class EntityOpener extends BasicOpener {
	private static final List<EntityOpener> OPENERS = new ArrayList<>();

	private String entitySelector;	
	
	@Exclude private EntitySelector selector;

	@Override
	public boolean open(ServerPlayerEntity player, Object... context) {
		if (selector==null || matches((Entity)context[0])) {
			return super.open(player, context);
		}
		return false;
	}
	private boolean matches(Entity e) {
		try {
			return e.equals(selector.getEntity(((LevelSetter)e.getCommandSource()).withHigherLevel(2)));
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
		if (OPENERS.contains(this) || OPENERS.add(this)) ActionInventoryMod.warn("Failed to add Item opener to list.");
	}
}