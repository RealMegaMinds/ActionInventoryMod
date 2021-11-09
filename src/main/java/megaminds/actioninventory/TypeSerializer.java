package megaminds.actioninventory;

import java.lang.reflect.Type;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TypeSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {
	private final TypeManager<T> manager;
	
	public TypeSerializer(TypeManager<T> manager) {
		this.manager = manager;
	}
	
	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		String type = object.get("type").getAsString();
		Class<? extends T> clazz = manager.get(type);
		if (clazz==null) {
			ActionInventoryMod.log(Level.WARN, "Failed to get class for type: "+typeOfT+" with name: "+type);
			return null;
		}
		return context.deserialize(json, clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = (JsonObject) context.serialize(src, Object.class);
		object.addProperty("type", manager.getTypeValue((Class<T>) src.getClass()));
		return object;
	}
}