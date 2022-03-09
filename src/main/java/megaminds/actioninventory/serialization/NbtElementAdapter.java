package megaminds.actioninventory.serialization;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.StringNbtWriter;

public class NbtElementAdapter extends TypeAdapter<NbtElement> {
	@Override
	public void write(JsonWriter out, NbtElement value) throws IOException {
		out.value(new StringNbtWriter().apply(value));
	}

	@Override
	public NbtElement read(JsonReader in) throws IOException {
		var s = in.nextString();
		if (s.isEmpty()) return null;
		try {
			return new StringNbtReader(new StringReader(s)).parseElement();
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}
}