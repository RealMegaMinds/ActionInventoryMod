package megaminds.actioninventory.serialization;

import static megaminds.actioninventory.util.JsonHelper.getOrDefault;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import megaminds.actioninventory.gui.SlotFunction;
import megaminds.actioninventory.gui.SlotFunction.InventoryType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class SlotFunctionSerializer implements JsonDeserializer<SlotFunction>, JsonSerializer<SlotFunction> {
	private static final String INDEX = "redirectIndex", TYPE = "inventoryType", NAME = "inventoryName";

	@Override
	public SlotFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();

		int index = getOrDefault(obj.get(INDEX), JsonElement::getAsInt, 0);
		String name = getOrDefault(obj.get(NAME), JsonElement::getAsString, (String)null);
		InventoryType type = getOrDefault(obj.get(TYPE), e->context.deserialize(e, InventoryType.class), InventoryType.PLAYER);
		if (type==InventoryType.GENERATED) throw new JsonParseException("Cannot deserialize slot redirects of type:"+type);
		
		return new SlotFunction() {
			@Override public String getName() {return name;}
			@Override public int getRedirectIndex() {return index;}
			@Override public InventoryType getType() {return type;}
			@Override
			public Slot apply(ServerPlayerEntity p) {
				ServerPlayerEntity real = name==null ? p : p.getServer().getPlayerManager().getPlayer(UUID.fromString(name));
				return switch (type) {
				case PLAYER -> new Slot(real.getInventory(), index, 0, 0);
				case ENDERCHEST -> new Slot(real.getEnderChestInventory(), index, 0, 0);
				case GENERATED -> throw new JsonParseException("Cannot deserialize slot redirects of type:"+type);
				};
			}
		};
	}

	@Override
	public JsonElement serialize(SlotFunction src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.addProperty(INDEX, src.getRedirectIndex());
		obj.add(TYPE, context.serialize(src.getType()));
		obj.addProperty(NAME, src.getName());
		return obj;
	}
}