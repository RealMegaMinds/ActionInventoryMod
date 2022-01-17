package megaminds.actioninventory.serialization.wrappers;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ValidatedAdapterWrapper implements TypeAdapterWrapper {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> TypeAdapter<T> wrapAdapter(@NotNull TypeAdapter<T> adapter, Gson gson, TypeToken<T> type) {
		if (!Validated.class.isAssignableFrom(type.getRawType())) return adapter;
		return new ValidatedAdapter(adapter);
	}

	private class ValidatedAdapter<T extends Validated> extends TypeAdapter<T> {
		private final TypeAdapter<T> delegate;
		
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