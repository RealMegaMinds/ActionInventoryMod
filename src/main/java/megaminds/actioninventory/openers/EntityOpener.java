package megaminds.actioninventory.openers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	private boolean failed;
	
	@Exclude private EntitySelector selector;

	@Override
	public boolean open(ServerPlayerEntity player, Object... context) {
		if (!failed && selector==null) {
			fixSelector();
		}

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

	public void fixSelector() {
		String whole = "@s"+Objects.requireNonNullElse(entitySelector, "").strip();

		try {
			this.selector = new EntitySelectorReader(new StringReader(whole)).read();
		} catch (CommandSyntaxException e) {
			ActionInventoryMod.warn("Failed to read entity selector for an EntityOpener.");
			e.printStackTrace();
			this.failed = true;
			this.selector = null;
		}
	}

	public boolean addToMap() {
		return OPENERS.contains(this) || OPENERS.add(this);
	}

	public void removeFromMap() {
		OPENERS.remove(this);
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
}