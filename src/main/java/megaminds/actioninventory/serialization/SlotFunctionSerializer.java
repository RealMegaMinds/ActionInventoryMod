package megaminds.actioninventory.serialization;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import eu.pb4.sgui.virtual.inventory.VirtualInventory;
import megaminds.actioninventory.util.NamedGuiLoader;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class SlotFunctionSerializer implements JsonDeserializer<Function<ServerPlayerEntity, Slot>> {
	private static final String TYPE = "type", PLAYER = "playerInventory", ENDERCHEST = "enderChest", NAMED_GUI = "gui";
	private static final String PLAYER_ID = "player", GUI_NAME = "guiName", INDEX = "index";

	@Override
	public Function<ServerPlayerEntity, Slot> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		
		int index = obj.has(INDEX) ? obj.get(INDEX).getAsInt() : 0;
		
		boolean isInventory;
		if (!obj.has(TYPE) || obj.get(TYPE).getAsString().equals(NAMED_GUI)) {
			if (!obj.has(GUI_NAME)) throw new JsonParseException("Slots of type: "+NAMED_GUI+" must have a value for: "+GUI_NAME);
			return p -> new Slot(new VirtualInventory(NamedGuiLoader.getGui(p, obj.get(GUI_NAME).getAsString())), index, 0, 0);
		} else if (obj.get(TYPE).getAsString().equals(PLAYER)) {
			isInventory = true;
		} else if (obj.get(TYPE).getAsString().equals(ENDERCHEST)) {
			isInventory = false;
		} else {
			throw new JsonParseException("No known slot type of: "+obj.get(TYPE).getAsString());
		}
		
		UUID uuid;
		if (!obj.has(PLAYER_ID) || obj.get(PLAYER_ID).toString().equals(PLAYER_ID)) {
			uuid = null;
		} else {
			uuid = UUID.fromString(obj.get(PLAYER_ID).getAsString());
		}
		return p -> {
			ServerPlayerEntity player = uuid==null ? p : p.getServer().getPlayerManager().getPlayer(uuid);
			return new Slot(isInventory ? player.getInventory() : player.getEnderChestInventory(), index, 0, 0);
		};
	}
}