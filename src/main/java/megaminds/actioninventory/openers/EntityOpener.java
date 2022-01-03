package megaminds.actioninventory.openers;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.LevelSetter;
import megaminds.actioninventory.util.Helper;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class EntityOpener extends BasicOpener {
	private static final String SELECTOR = "entitySelector";
	private static final List<EntityOpener> OPENERS = new ArrayList<>();

	private EntitySelector selector;

	@Override
	public boolean open(ServerPlayerEntity player, Object... context) {
		if (context[0] instanceof Entity e && (selector==null || matches(selector, e))) {
			return super.open(player, context);
		}
		return false;
	}

	private static boolean matches(EntitySelector s, Entity e) {
		try {
			return e.equals(s.getEntity(((LevelSetter)e.getCommandSource()).withHigherLevel(2)));
		} catch (CommandSyntaxException e1) {
			return false;
		}
	}

	@Override
	public BasicOpener fromJson(JsonObject obj, JsonDeserializationContext context) {
		if (obj.has(SELECTOR)) {
			String selector = "@s"+obj.get(SELECTOR).getAsString().trim();
			try {
				this.selector = new EntitySelectorReader(new StringReader(selector)).read();
			} catch (CommandSyntaxException e1) {
				ActionInventoryMod.warn("Failed to read entity selector for an EntityOpener.");
			}
		}
		return this;
	}

	@Override
	public Opener getType() {
		return Opener.ENTITY;
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
}