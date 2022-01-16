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

import megaminds.actioninventory.util.annotations.Poly;

/**
 * This returns the type adapter that is used for the first parent as the adapter for each subclass.
 */
public class PolyAdapterFactory implements TypeAdapterFactory {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		Class<?> raw = type.getRawType();
		if (raw.isAnnotationPresent(Poly.class)) {
			Class<?> root = findRoot(raw);
			if (root==raw) {
				return new PolyAdapter(gson, root);
			}
			return (TypeAdapter<T>) gson.getAdapter(root);
		}
		return null;
	}
	
	private static Class<?> findRoot(Class<?> start) {
		Class<?> parent = start.getSuperclass();
		return parent!=null&&parent.isAnnotationPresent(Poly.class) ? findRoot(parent) : start;
	}
	
	private final class PolyAdapter<T> extends TypeAdapter<T> {
		private final Map<Class<?>, String> classToName;
		private final Map<Class<?>, TypeAdapter<?>> classToAdapter;
		private final Map<String, TypeAdapter<?>> nameToAdapter;
		
		private PolyAdapter(Gson gson, Class<?> root) {
			this.classToName = new HashMap<>();
			this.classToAdapter = new HashMap<>();
			this.nameToAdapter = new HashMap<>();
			retrieveClasses(root);
			loadAdapters(gson);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void write(JsonWriter out, T value) throws IOException {
			Class<?> raw = value.getClass();
			TypeAdapter<T> adapter = ((TypeAdapter<T>)classToAdapter.get(raw));
			if (adapter==null) {
				throw new IllegalArgumentException("Failed to find TypeAdapter for class: "+raw);
			}
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
				throw new IllegalArgumentException("Failed to find TypeAdapter for name: "+type);
			}
			return adapter.fromJsonTree(obj);
		}
		
		private void loadAdapters(Gson gson) {
			for (Entry<Class<?>, String> e : classToName.entrySet()) {
				TypeAdapter<?> adapter = gson.getDelegateAdapter(PolyAdapterFactory.this, TypeToken.get(e.getKey()));
				
				classToAdapter.put(e.getKey(), adapter);
				nameToAdapter.put(e.getValue(), adapter);
			}
		}
		
		private void retrieveClasses(Class<?> clazz) {
			if (!(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))) classToName.put(clazz, getName(clazz));
			
			if (clazz.isSealed()) {
				for (Class<?> c : clazz.getPermittedSubclasses()) {
					retrieveClasses(c);
				}
			}
		}
		
		private static String getName(Class<?> clazz) {
			String s = clazz.getAnnotation(Poly.class).value();
			return s.isBlank() ? clazz.getSimpleName() : s;
		}
	}
}