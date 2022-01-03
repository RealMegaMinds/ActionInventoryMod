package megaminds.actioninventory.serialization;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import megaminds.actioninventory.openers.BasicOpener;
import megaminds.actioninventory.openers.Opener;

public class BasicOpenerSerializer implements JsonDeserializer<BasicOpener> {
	private static final String TYPE = "type", NAME = "guiName";
	
	@Override
	public BasicOpener deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		if (!obj.has(TYPE)) throw new JsonParseException("Openers must have a type.");
		if (!obj.has(NAME)) throw new JsonParseException("Openers must have a name for the gui they open.");
		BasicOpener opener = context.<Opener>deserialize(obj.get(TYPE), Opener.class).create();
		opener.setName(obj.get(NAME).getAsString());
		return opener.fromJson(obj, context);
	}
}