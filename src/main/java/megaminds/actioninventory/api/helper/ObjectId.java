package megaminds.actioninventory.api.helper;

import net.minecraft.util.Identifier;

public final class ObjectId {
	public static final String SEPERATOR = "@";
	public final Identifier type;
	public Identifier id;
	
	public ObjectId(Identifier type, Identifier id) {
		this.type = type;
		this.id = id;
	}
	
	public static ObjectId fromString(String id) {
		String[] arr = id.split(SEPERATOR);
		return new ObjectId(new Identifier(arr[0]), new Identifier(arr[1]));
	}
	
	@Override
	public boolean equals(Object obj) {
        return this==obj || (obj instanceof ObjectId oId && type.equals(oId.type) && id.equals(oId.id));
	}
	
	@Override
	public String toString() {
		return type.toString()+SEPERATOR+id.toString();
	}
}