package megaminds.actioninventory.serialization;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import com.google.gson.internal.$Gson$Types;

import megaminds.actioninventory.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NullOptionalSerializer implements JsonDeserializer<Optional<?>>, JsonSerializer<Optional<?>>{
	private boolean complexKeyEnabled;
	public NullOptionalSerializer(boolean isComplexKeyMappingEnabled) {
		complexKeyEnabled = isComplexKeyMappingEnabled;
	}
	
	@Override
	public JsonElement serialize(Optional<?> src, Type typeOfSrc, JsonSerializationContext context) {
		if (src.isEmpty()) {
			return new JsonArray();
		} else {
			return context.serialize(src.get());
		}
	}

	@Override
	public Optional<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (!(typeOfT instanceof ParameterizedType)) throw new JsonParseException("OptionalSerializer must be supplied with a ParameterizedType for deserialization. Use JsonHelper.optional()");

		Type realType = ((ParameterizedType)typeOfT).getActualTypeArguments()[0];
		Class<?> raw = $Gson$Types.getRawType(realType);
		if (json.isJsonArray()) {
			JsonArray arr = json.getAsJsonArray();
			if (raw.isArray() || Collection.class.isAssignableFrom(raw) || complexKeyEnabled && Map.class.isAssignableFrom(raw)) {
				return Optional.ofNullable(context.deserialize(json, realType));
			} else if (arr.isEmpty()) {
				return Optional.empty();
			} else {
				return Optional.ofNullable(context.deserialize(json.getAsJsonArray().get(0), realType));
			}
		} else {
			return Optional.ofNullable(context.deserialize(json, realType));
		}
	}
}