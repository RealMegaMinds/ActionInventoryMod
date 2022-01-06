package megaminds.actioninventory.consumables;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class BasicConsumable {
	private boolean requireFull;

	/**
	 * Returns true if the player has paid or can pay the full amount.
	 */
	public abstract boolean canConsumeFull(ServerPlayerEntity player, NbtElement storage);
	/**
	 * Consumes from the player, and returns the corrected storage.
	 */
	public abstract NbtElement consume(ServerPlayerEntity player, NbtElement storage);
	
	public abstract BasicConsumable fromJson(JsonObject obj, JsonDeserializationContext context);
	public abstract JsonObject toJson(JsonObject obj, JsonSerializationContext context);
	public abstract Consumable getType();
	
	public final boolean getRequireFull() {
		return requireFull;
	}
	public void setRequireFull(boolean require) {
		requireFull = require;
	}
	public final void requireFull() {
		setRequireFull(true);
	}
}