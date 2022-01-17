package megaminds.actioninventory.serialization.wrappers;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

public interface TypeAdapterWrapper {
	<T> TypeAdapter<T> wrapAdapter(@NotNull TypeAdapter<T> adapter, Gson gson, TypeToken<T> type);
}