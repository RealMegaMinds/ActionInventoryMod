package megaminds.actioninventory.inventory.openers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class OpenerSerializer implements JsonDeserializer<Opener> {

	@Override
	public Opener deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		Opener.Type type = context.deserialize(object.get("type"), Opener.Type.class);
		return context.deserialize(json, type.clazz);
	}
}