package megaminds.actioninventory.serialization;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
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
 * Nothing - Null - Unspecified
 * [] - Empty Optional - Must not exist
 * else - Present Optional - Must exist
 */
public class NullableOptionalAdapterFactory implements TypeAdapterFactory {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (type.getRawType()==Optional.class) {
			TypeToken<?> inner = TypeToken.get(((ParameterizedType)type.getType()).getActualTypeArguments()[0]);
			return new NullableOptionalAdapter(gson.getAdapter(inner), inner.getRawType());
		}
		return null;
	}

	private static class NullableOptionalAdapter<T> extends TypeAdapter<Optional<T>> {
		private TypeAdapter<T> delegate;
		private boolean delegateIsArray;
		private NullableOptionalAdapter(TypeAdapter<T> delegate, Class<?> innerType) {
			this.delegate = delegate;
			if (innerType.isArray() || Collection.class.isAssignableFrom(innerType)) {// || Map.class.isAssignableFrom(innerType)) {
				delegateIsArray = true;
			}
		}
		@Override
		public void write(JsonWriter out, Optional<T> value) throws IOException {
			if (value==null) {
				out.nullValue();
			} else if (value.isEmpty()) {
				out.beginArray();
				out.endArray();
			} else {
				delegate.write(out, value.orElseThrow());
			}
		}
		@NotNull
		@Override
		public Optional<T> read(JsonReader in) throws IOException {
			//START HERE
			
			if (in.peek()==JsonToken.NULL) {
				in.nextNull();
				return null;
			} else if (in.peek()==JsonToken.BEGIN_ARRAY) {
				in.beginArray();
				if (in.peek()==JsonToken.END_ARRAY && !delegateIsArray) {
					in.endArray();
					return Optional.empty();
				} else if (in.peek()==JsonToken.END_ARRAY) {
					in.endArray();
					return Optional.of(delegate.fromJson("[]"));
				} else {
					return Optional.of(null);
				}
			} else {
				return Optional.of(delegate.read(in));
			}
		}
	}
	
	//[] && !array - empty optional
	//[] && array - empty list
}