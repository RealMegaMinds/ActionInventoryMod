package megaminds.actioninventory.openers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import megaminds.actioninventory.util.NamedGuiLoader;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class BasicOpener {
	private String name;
	
	public boolean open(ServerPlayerEntity player, Object... context) {
		return NamedGuiLoader.openGui(player, name);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Implementors should return themselves.
	 */
	public abstract BasicOpener fromJson(JsonObject obj, JsonDeserializationContext context);
	public abstract JsonObject toJson(JsonObject obj, JsonSerializationContext context);
	public abstract Opener getType();
	public abstract boolean addToMap();
	public abstract void removeFromMap();
}