package megaminds.actioninventory.serialization;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.pb4.sgui.api.gui.AnvilInputGui;
import megaminds.actioninventory.mixin.EnderChestInventoryMixin;
import megaminds.actioninventory.util.Helper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

public class ScreenSerializer implements JsonSerializer<AnvilInputGui> {
	private static final Map<String, Inventory> NAMED_INVENTORIES = new HashMap<>();
	
	//TODO working on this
	//link: https://github.com/Patbox/sgui/blob/5d442ce48363e362b90fc92017b82253d3275c21/src/main/java/eu/pb4/sgui/api/gui/BaseSlotGui.java#L9
	
	@Override
	public JsonElement serialize(AnvilInputGui gui, Type type, JsonSerializationContext context) {
		return null;
	}
	
	enum InventoryType {
		PLAYER, ENDER, NAMED;
	};
	
	class InventorySerializer implements JsonSerializer<Inventory>, JsonDeserializer<Inventory> {
		@Override
		public Inventory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			return switch ((InventoryType)context.deserialize(obj.get("type"), InventoryType.class)) {
			case PLAYER -> ((ServerPlayerEntity)context.deserialize(obj.get("player"), ServerPlayerEntity.class)).getInventory();
			case ENDER -> ((ServerPlayerEntity)context.deserialize(obj.get("player"), ServerPlayerEntity.class)).getEnderChestInventory();
			case NAMED -> NAMED_INVENTORIES.get(obj.get("name").getAsString());
			default -> null;
			};
		}
		@Override
		public JsonElement serialize(Inventory src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject obj = new JsonObject();
			if (src instanceof PlayerInventory inv) {
				obj.add("type", context.serialize(InventoryType.PLAYER));
				obj.add("player", context.serialize(inv.player, ServerPlayerEntity.class));
			} else if (src instanceof EnderChestInventory inv) {
				obj.add("type", context.serialize(InventoryType.ENDER));
				obj.add("player", context.serialize(((EnderChestInventoryMixin)(Object)inv).getOwner(), ServerPlayerEntity.class));
			} else if (NAMED_INVENTORIES.containsValue(src)) {
				obj.add("type", context.serialize(InventoryType.NAMED));
				obj.addProperty("name", Helper.getFirst(NAMED_INVENTORIES.entrySet(), e->e.getValue().equals(src)).getKey());
			}
			return null;
		}
	}
	
	class PlayerSerializer implements JsonSerializer<ServerPlayerEntity>, JsonDeserializer<ServerPlayerEntity> {
		private static PlayerManager manager;
		
		public static void setPlayerManager(PlayerManager manager) {
			PlayerSerializer.manager = manager;
		}
		
		@Override
		public JsonElement serialize(ServerPlayerEntity src, Type typeOfSrc, JsonSerializationContext context) {
			return context.serialize(src.getUuid().toString());
		}
		@Override
		public ServerPlayerEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return manager.getPlayer(UUID.fromString(json.getAsString()));
		}
	}
}