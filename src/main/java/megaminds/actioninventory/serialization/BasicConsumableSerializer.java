package megaminds.actioninventory.serialization;

import static megaminds.actioninventory.util.JsonHelper.getOrError;
import static megaminds.actioninventory.util.JsonHelper.getDo;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import megaminds.actioninventory.consumables.BasicConsumable;
import megaminds.actioninventory.consumables.Consumable;

public class BasicConsumableSerializer implements JsonDeserializer<BasicConsumable>, JsonSerializer<BasicConsumable> {
	private static final String REQUIRE_FULL = "requireFullAmount", TYPE = "type";

	@Override
	public BasicConsumable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		
		BasicConsumable consumable = getOrError(obj.get(TYPE), e->context.<Consumable>deserialize(e, Consumable.class), "Consumables must have a type").get();
		getDo(obj.get(REQUIRE_FULL), JsonElement::getAsBoolean, consumable::setRequireFull);
		return consumable.fromJson(obj, context);
	}

	@Override
	public JsonElement serialize(BasicConsumable src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.add(TYPE, context.serialize(src.getType()));
		obj.addProperty(REQUIRE_FULL, src.getRequireFull());
		return src.toJson(obj, context);
	}
}
