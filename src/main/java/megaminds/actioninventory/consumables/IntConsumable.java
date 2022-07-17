package megaminds.actioninventory.consumables;

import static megaminds.actioninventory.misc.Enums.COMPLETE;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import megaminds.actioninventory.util.Helper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

/**@since 3.1*/
public abstract sealed class IntConsumable extends BasicConsumable permits ItemConsumable {
	private static final String AMOUNT_KEY = "amount";
	/**This is the paid amount*/
	private int amount;
	
	protected IntConsumable() {}

	protected IntConsumable(int amount) {
		this.amount = amount;
	}

	public boolean hasConsumedFull(@Nullable NbtCompound storage) {
		return Helper.getBoolean(storage, COMPLETE);
	}

	@Override
	public boolean canConsumeFull(MinecraftServer server, UUID player, @Nullable NbtCompound storage) {
		return Helper.getBoolean(storage, COMPLETE) || canConsumeFull(server, player, amount-Helper.getInt(storage, AMOUNT_KEY));
	}

	@Override
	public boolean consume(MinecraftServer server, UUID player, @NotNull NbtCompound storage){
		var requireFullB = getRequireFull().orElse(false);
		var hasPaid = Helper.getBoolean(storage, COMPLETE);

		var left = amount-storage.getInt(AMOUNT_KEY);

		//the full amount has not been paid and [the full amount is not required or (the full amount is required and can be paid)].
		if (!hasPaid && (!requireFullB || canConsumeFull(server, player, left))) {
			left = consume(server, player, left);
			hasPaid = left<=0;
		}

		//the full amount was paid or has now been paid
		if (hasPaid) {
			storage.remove(AMOUNT_KEY);
			storage.putBoolean(COMPLETE, true);
		} else {
			storage.putInt(AMOUNT_KEY, amount-left);
		}

		return hasPaid;
	}

	public abstract boolean canConsumeFull(MinecraftServer server, UUID player, int leftToConsume);
	/**
	 * Returns how much is left
	 */
	public abstract int consume(MinecraftServer server, UUID player, int leftToConsume);

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}