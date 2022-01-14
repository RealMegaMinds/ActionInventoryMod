package megaminds.actioninventory.consumables;

import com.google.gson.annotations.JsonAdapter;

import megaminds.actioninventory.misc.Validated;
import megaminds.actioninventory.serialization.PolymorphicTypeAdapterFactory;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;

@JsonAdapter(PolymorphicTypeAdapterFactory.class)
public abstract class BasicConsumable implements Validated {
	private boolean requireFull;
	
	protected BasicConsumable() {}

	/**
	 * Returns true if the player has paid or can pay the full amount.
	 */
	public abstract boolean canConsumeFull(ServerPlayerEntity player, NbtElement storage);
	/**
	 * Consumes from the player, and returns the corrected storage.
	 */
	public abstract NbtElement consume(ServerPlayerEntity player, NbtElement storage);
	/**
	 * Returns the name of the storage this consumable accesses
	 */
	public abstract String getStorageName();
	
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