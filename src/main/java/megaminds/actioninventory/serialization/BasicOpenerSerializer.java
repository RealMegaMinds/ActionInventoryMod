package megaminds.actioninventory.serialization;

import static megaminds.actioninventory.util.JsonHelper.getOrError;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import megaminds.actioninventory.openers.BasicOpener;
import megaminds.actioninventory.openers.Opener;

public class BasicOpenerSerializer implements JsonDeserializer<BasicOpener>, JsonSerializer<BasicOpener> {
	private static final String TYPE = "type", NAME = "guiName";
	
	@Override
	public BasicOpener deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		BasicOpener opener = getOrError(obj.get(TYPE), e->context.<Opener>deserialize(e, Opener.class), "Openers must have a type.").create();
		opener.setName(getOrError(obj.get(NAME), JsonElement::getAsString, "Openers must have a name for the gui they open."));
		return opener.fromJson(obj, context);
	}

	@Override
	public JsonElement serialize(BasicOpener src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.add(TYPE, context.serialize(src.getType()));
		obj.addProperty(NAME, src.getName());
		return src.toJson(obj, context);
	}
}