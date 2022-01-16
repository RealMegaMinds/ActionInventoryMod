package megaminds.actioninventory.serialization.wrappers;

import java.io.IOException;
import java.lang.reflect.Field;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import megaminds.actioninventory.util.annotations.Instanced;

public class InstancedAdapterWrapper implements TypeAdapterWrapper {
	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> TypeAdapter<T> wrapAdapter(@NotNull TypeAdapter<T> adapter, Gson gson, TypeToken<T> type) {
		Class<?> raw = type.getRawType();
		if (!raw.isAnnotationPresent(Instanced.class)) return adapter;
		
		T instance = null;
		try {
			Field f = raw.getDeclaredField("INSTANCE");
			f.setAccessible(true);	//NOSONAR necessary
			instance = (T) f.get(null);
		} catch (Exception e) {/*Handled below*/}
		if (instance==null) return adapter;

		return new InstancedAdapter<>(adapter, instance);
	}
	
	private class InstancedAdapter<T> extends TypeAdapter<T> {
		private final TypeAdapter<T> delegate;
		private final T instance;

		private InstancedAdapter(TypeAdapter<T> delegate, T instance) {
			this.delegate = delegate;
			this.instance = instance;
		}
		
		@Override
		public void write(JsonWriter out, T value) throws IOException {
			delegate.write(out, value);
		}
		
		@Override
		public T read(JsonReader in) throws IOException {
			if (in.peek()==JsonToken.NULL) return null;
			
			in.skipValue();
//			delegate.read(in);	//Not sure which is best
			
			return instance;
		}
	}
}