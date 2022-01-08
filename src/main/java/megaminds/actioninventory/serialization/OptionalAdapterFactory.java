package megaminds.actioninventory.serialization;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Nothing - Empty Optional - Unspecified
 * else - Present Optional - Specified
 */
public class OptionalAdapterFactory implements TypeAdapterFactory {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (type.getRawType()==Optional.class) {
			return new OptionalAdapter(gson.getAdapter(TypeToken.get(((ParameterizedType)type.getType()).getActualTypeArguments()[0])));
		}
		return null;
	}

	private static class OptionalAdapter<T> extends TypeAdapter<Optional<T>> {
		private TypeAdapter<T> delegate;
		private OptionalAdapter(TypeAdapter<T> delegate) {
			this.delegate = delegate;
		}
		@Override
		public void write(JsonWriter out, Optional<T> value) throws IOException {
			if (value==null || value.isEmpty()) {
				out.nullValue();
			} else {
				delegate.write(out, value.orElseThrow());
			}
		}
		@NotNull
		@Override
		public Optional<T> read(JsonReader in) throws IOException {
			if (in.peek()==JsonToken.NULL) {
				in.nextNull();
				return Optional.empty();
			} else {
				return Optional.of(delegate.read(in));
			}
		}
	}
}