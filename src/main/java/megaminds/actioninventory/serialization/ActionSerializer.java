package megaminds.actioninventory.serialization;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import megaminds.actioninventory.actions.Action;
import megaminds.actioninventory.actions.ErroredAction;

public class ActionSerializer implements JsonDeserializer<Action>, JsonSerializer<Action> {
	//TODO Move this to a better place and make register methods
	private static final Map<String, BiFunction<JsonObject, JsonDeserializationContext, Action>> converters = new HashMap<>();

	@Override
	public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonNull()) return Action.EMPTY;
		var obj = json.getAsJsonObject();
		if (!obj.has("type")) return new ErroredAction("Action must contain type");

		var type = obj.get("type").getAsString();
		var converter = converters.get(type);
		if (converter==null) return new ErroredAction("No converter for type: "+type);

		return converter.apply(obj, context);
	}

	@Override
	public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
		if (src==null) return JsonNull.INSTANCE;
		var obj = new JsonObject();
		src.appendFields(obj, context);
		return obj;
	}
}