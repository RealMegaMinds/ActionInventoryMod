package megaminds.actioninventory.consumables;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Poly;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Poly
@Getter
public abstract sealed class BasicConsumable implements Validated permits XpConsumable, IntConsumable {
	/**True->Full amount is needed to consume any, false->will consume as much as possible*/
	private TriState requireFull = TriState.DEFAULT;

	/**
	 * Returns true if the player has paid or can pay the full amount.
	 */
	public abstract boolean canConsumeFull(MinecraftServer server, UUID player, @Nullable NbtCompound storage);
	/**
	 * Consumes maximum from player up to the correct amount.
	 * <br>
	 * Returns true if the full amount was consumed.
	 */
	public abstract boolean consume(MinecraftServer server, UUID player, @NotNull NbtCompound storage);
	/**
	 * Returns the name of the storage this consumable accesses
	 */
	public abstract String getStorageName();

	public abstract BasicConsumable copy();
}