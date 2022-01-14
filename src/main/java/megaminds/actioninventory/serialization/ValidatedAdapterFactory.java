package megaminds.actioninventory.serialization;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import megaminds.actioninventory.misc.Validated;

public class ValidatedAdapterFactory implements TypeAdapterFactory {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		Class<?> raw = type.getRawType();
		if (!Validated.class.isAssignableFrom(raw)) return null;
		
		TypeAdapter<T> adapter = gson.getDelegateAdapter(this, type);
		return (TypeAdapter<T>) new ValidatedAdapter(adapter);
	}

	private class ValidatedAdapter<T extends Validated> extends TypeAdapter<T> {
		private TypeAdapter<T> delegate;
		
		private ValidatedAdapter(TypeAdapter<T> delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public void write(JsonWriter out, T value) throws IOException {
			delegate.write(out, value);
		}

		@Override
		public T read(JsonReader in) throws IOException {
			T t = delegate.read(in);
			if (t!=null) t.validate();
			return t;
		}
	}
}