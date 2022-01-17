package megaminds.actioninventory.serialization;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import megaminds.actioninventory.util.annotations.Poly;
import megaminds.actioninventory.util.annotations.PolyName;

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
			if (!root.isSealed()) throw new IllegalArgumentException("Classes must be sealed to use @Poly");
			if (root.isInterface()) throw new IllegalArgumentException("Types cannot be an interface to use @Poly");
			
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
		private final Gson gson;
		private final BiMap<Class<?>, String> classToName;
		private final Map<Class<?>, TypeAdapter<?>> classToAdapter;
		
		private PolyAdapter(Gson gson, Class<?> root) {
			this.gson = gson;
			this.classToName = HashBiMap.create();
			this.classToAdapter = new HashMap<>();
			retrieveClasses(root);
		}
		
		@Override
		public void write(JsonWriter out, T value) throws IOException {
			if (value==null) {
				out.nullValue();
				return;
			}
			Class<?> raw = value.getClass();
			JsonElement obj = getAdapter(raw).toJsonTree(value);
			obj.getAsJsonObject().addProperty("type", classToName.get(raw));
			Streams.write(obj, out);
		}

		@Override
		public T read(JsonReader in) throws IOException {
			if (in.peek()==JsonToken.NULL) return null;
			JsonObject obj = Streams.parse(in).getAsJsonObject();
			JsonElement type = obj.remove("type");
			if (type==null) {
				throw new IllegalArgumentException("No type was specified!");
			}
			TypeAdapter<T> adapter = getAdapter(type.getAsString());
			return adapter.fromJsonTree(obj);
		}
		
		@SuppressWarnings("unchecked")
		private TypeAdapter<T> getAdapter(Class<?> clazz) {
			return (TypeAdapter<T>) classToAdapter.computeIfAbsent(clazz, c->gson.getDelegateAdapter(PolyAdapterFactory.this, TypeToken.get(c)));
		}
		
		private TypeAdapter<T> getAdapter(String name) {
			Class<?> clazz = classToName.inverse().get(name);
			if (clazz==null) {
				throw new IllegalArgumentException("Unknown type: "+name);
			}
			return getAdapter(clazz);
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
			PolyName n = clazz.getAnnotation(PolyName.class);
			return n==null ? clazz.getSimpleName() : n.value();
		}
	}
}