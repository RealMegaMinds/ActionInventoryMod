package megaminds.actioninventory.consumables;

import static megaminds.actioninventory.misc.Enums.COMPLETE;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.util.Helper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

/**@since 3.1*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract sealed class NumberConsumable extends BasicConsumable permits ItemConsumable {
	private static final String AMOUNT_KEY = "amount";
	/**This is the paid amount*/
	private long amount;

	public boolean hasConsumedFull(@Nullable NbtCompound storage) {
		var paid = storage!=null ? storage.getLong(AMOUNT_KEY) : 0;
		return (storage!=null&&storage.getBoolean(COMPLETE)) || paid>=amount;
	}

	@Override
	public boolean canConsumeFull(MinecraftServer server, UUID player, @Nullable NbtCompound storage) {
		return Helper.getBoolean(storage, COMPLETE) || canConsume(server, player, amount-Helper.getLong(storage, AMOUNT_KEY));
	}

	@Override
	public boolean consume(MinecraftServer server, UUID player, @NotNull NbtCompound storage) {
		if (storage.contains(COMPLETE)) return true;

		var left = consume(server, player, amount-storage.getLong(AMOUNT_KEY));

		if (left<=0) {
			storage.remove(AMOUNT_KEY);
			storage.putBoolean(COMPLETE, true);
			return true;
		} else {
			storage.putLong(AMOUNT_KEY, amount-left);
			return false;
		}
	}

	public abstract boolean canConsume(MinecraftServer server, UUID player, long leftToConsume);
	/**
	 * Returns how much is left
	 */
	public abstract long consume(MinecraftServer server, UUID player, long leftToConsume);
}