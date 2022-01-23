package megaminds.actioninventory.consumables;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Poly
public abstract sealed class BasicConsumable implements Validated permits XpConsumable, NumberConsumable {
	private boolean requireFull;
	
	/**
	 * Returns true if the player has paid or can pay the full amount.
	 */
	public abstract boolean canConsumeFull(ServerPlayerEntity player, NbtElement storage);
	/**
	 * Consumes from the player, and corrects storage if saveAmount is true.
	 * @param storage
	 * The current storage.
	 * @return The corrected storage if saveAmount is true otherwise null
	 */
	public abstract NbtElement consume(ServerPlayerEntity player, NbtElement storage, boolean saveAmount);
	/**
	 * Returns the name of the storage this consumable accesses
	 */
	public abstract String getStorageName();
	
	public abstract BasicConsumable copy();
	
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