package megaminds.actioninventory.inventory.requirements;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class RequirementSerializer implements JsonDeserializer<Requirement> {

	@Override
	public Requirement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		Requirement.Type type = context.deserialize(object.get("type"), Requirement.Type.class);
		return context.deserialize(json, type.clazz);
	}
}