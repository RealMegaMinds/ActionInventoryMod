package megaminds.actioninventory.api.util;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonObject;

import net.minecraft.util.Identifier;

/**
 * A basic identification for ActionObjects. Holds the type of the object and its id.
 */
public final class ObjectId {
	private static final String SEPARATOR = "#";

	public final Identifier type;
	public Identifier id;

	public ObjectId(Identifier type, Identifier id) {
		this.type = type;
		this.id = id;
	}

	public void writeData(JsonObject obj) {
		obj.addProperty("type", type.toString());
		obj.addProperty("id", id.toString());
	}

	public static ObjectId readData(JsonObject obj) {
		return new ObjectId(new Identifier(obj.get("type").getAsString()), new Identifier(obj.get("id").getAsString()));
	}

	@Override
	public boolean equals(Object obj) {
		return this==obj || (obj instanceof ObjectId oId && type.equals(oId.type) && id.equals(oId.id));
	}
	
	@Internal
	public static ObjectId fromString(String s) {
		String[] arr = s.split(SEPARATOR);
		return new ObjectId(new Identifier(arr[0]), new Identifier(arr[1]));
	}

	@Internal
	@Override
	public String toString() {
		return type.toString()+SEPARATOR+id.toString();
	}
}