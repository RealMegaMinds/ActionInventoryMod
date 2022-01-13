package megaminds.actioninventory.serialization;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import megaminds.actioninventory.util.annotations.TypeName;

/*
 * Classes MUST be sealed. Subclasses must be sealed or final.<br>
 * Only the parent class requires @JsonAdapter annotation.<br>
 * Use @SerializedName to change the name of the type.
 */
public class PolymorphicTypeAdapterFactory implements TypeAdapterFactory {
	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		Class<?> raw = type.getRawType();
		
		Map<Class<?>, String> classToName = new HashMap<>();
		Map<Class<?>, TypeAdapter<?>> classToAdapter = new HashMap<>();
		Map<String, TypeAdapter<?>> nameToAdapter = new HashMap<>();

		retrieveClasses(raw, classToName);

		for (Entry<Class<?>, String> e : classToName.entrySet()) {
			TypeAdapter<?> adapter = gson.getDelegateAdapter(this, TypeToken.get(e.getKey()));
			
			classToAdapter.put(e.getKey(), adapter);
			nameToAdapter.put(e.getValue(), adapter);
		}
		return (TypeAdapter<T>) new PolymorphicTypeAdapter<>(classToName, classToAdapter, nameToAdapter).nullSafe();
	}
	
	private void retrieveClasses(Class<?> clazz, Map<Class<?>, String> map) {
		int mod = clazz.getModifiers();
		if (!(Modifier.isAbstract(mod) || Modifier.isInterface(mod))) {
			map.put(clazz, getName(clazz));
		}
		
		if (Modifier.isFinal(clazz.getModifiers())) {
			return;
		} else if (clazz.isSealed()) {
			Class<?>[] subclasses = clazz.getPermittedSubclasses();
			for (Class<?> c : subclasses) {
				retrieveClasses(c, map);
			}
			return;
		}
		throw new IllegalArgumentException("Classes using PolymorphicTypeAdapterFactory must be final or sealed.");
	}
	
	
	private static String getName(Class<?> clazz) {
		if (clazz.isAnnotationPresent(TypeName.class)) {
			return clazz.getAnnotation(TypeName.class).value();
		}
		return clazz.getSimpleName();
	}
	
	private final class PolymorphicTypeAdapter<T> extends TypeAdapter<T> {
		private final Map<Class<?>, String> classToName;
		private final Map<Class<?>, TypeAdapter<?>> classToAdapter;
		private final Map<String, TypeAdapter<?>> nameToAdapter;
		
		private PolymorphicTypeAdapter(Map<Class<?>, String> classToName, Map<Class<?>, TypeAdapter<?>> classToAdapter, Map<String, TypeAdapter<?>> nameToAdapter) {
			this.classToName = classToName;
			this.classToAdapter = classToAdapter;
			this.nameToAdapter = nameToAdapter;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void write(JsonWriter out, T value) throws IOException {
			Class<?> raw = value.getClass();
			TypeAdapter<T> adapter = ((TypeAdapter<T>)classToAdapter.get(raw));
			JsonElement obj = adapter.toJsonTree(value);
			obj.getAsJsonObject().addProperty("type", classToName.get(raw));
			Streams.write(obj, out);
		}

		@SuppressWarnings("unchecked")
		@Override
		public T read(JsonReader in) throws IOException {
			JsonObject obj = Streams.parse(in).getAsJsonObject();
			String type = obj.remove("type").getAsString();
			TypeAdapter<T> adapter = (TypeAdapter<T>) nameToAdapter.get(type);
			if (adapter==null) {
				throw new IllegalArgumentException("Failed to find Class for: "+type);
			}
			return adapter.fromJsonTree(obj);
		}
	}
}