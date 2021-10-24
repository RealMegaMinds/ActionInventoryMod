package megaminds.testmod.inventory.actions;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ActionSerializer implements JsonDeserializer<Action> {

	@Override
	public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		Action.Type type = context.deserialize(object.get("type"), Action.Type.class);
		return context.deserialize(json, type.clazz);
	}
}