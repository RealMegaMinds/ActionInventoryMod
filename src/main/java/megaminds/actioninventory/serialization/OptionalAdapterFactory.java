package megaminds.actioninventory.serialization;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class OptionalAdapterFactory implements TypeAdapterFactory {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (type.getRawType()==Optional.class) {
			TypeAdapter<?> delegate = gson.getAdapter(TypeToken.get(((ParameterizedType)type.getType()).getActualTypeArguments()[0]));
			return new OptionalAdapter(delegate);
		}
		return null;
	}

	/**
	 * Null==Unspecified
	 * !Null==Specified
	 */
	private static class OptionalAdapter<T> extends TypeAdapter<Optional<T>> {
		private TypeAdapter<T> delegate;
		private OptionalAdapter(TypeAdapter<T> delegate) {
			this.delegate = delegate;
		}
		@Override
		public void write(JsonWriter out, Optional<T> value) throws IOException {
			if (value==null) {
				out.nullValue();
				return;
			}
			
			out.beginArray();
			if (value.isPresent()) delegate.write(out, value.orElseThrow());
			out.endArray();
		}

		@Override
		public Optional<T> read(JsonReader in) throws IOException {
			JsonToken next = in.peek();
			if (next==JsonToken.NULL) {
				in.nextNull();
				return null;
			} else if (next==JsonToken.BEGIN_ARRAY) {
				in.beginArray();
				if (in.peek()==JsonToken.END_ARRAY) {
					in.endArray();
					return Optional.empty();
				}
				Optional<T> op = Optional.of(delegate.read(in));
				in.endArray();
				return op;
			} else {
				return Optional.ofNullable(delegate.read(in));
			}
		}
	}
}