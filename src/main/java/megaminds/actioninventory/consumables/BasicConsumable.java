package megaminds.actioninventory.consumables;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Poly;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Poly
public abstract sealed class BasicConsumable implements Validated permits XpConsumable, NumberConsumable {
	private TriState requireFull = TriState.DEFAULT;

	/**
	 * Returns true if the player has paid or can pay the full amount.
	 */
	public abstract boolean canConsumeFull(MinecraftServer server, UUID player, @Nullable NbtCompound storage);
	/**
	 * Consumes from the player, and corrects storage if saveAmount is true.
	 * @param storage
	 * The current storage.
	 * @return The corrected storage if saveAmount is true otherwise null
	 */
	public abstract void consume(MinecraftServer server, UUID player, @NotNull NbtCompound storage);
	/**
	 * Returns the name of the storage this consumable accesses
	 */
	public abstract String getStorageName();

	public abstract BasicConsumable copy();

	public final TriState isRequireFull() {
		return requireFull;
	}
	public final void setRequireFull(TriState require) {
		requireFull = require;
	}
	public final void requireFull() {
		setRequireFull(TriState.TRUE);
	}
}