package megaminds.actioninventory.serialization;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import net.fabricmc.fabric.api.util.TriState;

public class TriStateAdapter extends TypeAdapter<TriState> {
	@Override
	public TriState read(JsonReader in) throws IOException {
		if (in.peek()==JsonToken.NULL) {
			in.nextNull();
			return TriState.DEFAULT;
		}
		return TriState.of(in.nextBoolean());
	}

	@Override
	public void write(JsonWriter out, TriState value) throws IOException {
		out.value(value==null ? null : value.getBoxed());
	}
}