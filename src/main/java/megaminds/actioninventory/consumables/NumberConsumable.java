package megaminds.actioninventory.consumables;

import static megaminds.actioninventory.misc.Enums.COMPLETE;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

/**@since 3.1*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract sealed class NumberConsumable extends BasicConsumable permits ItemConsumable {
	private static final String AMOUNT_KEY = "amount";
	/**This is the paid amount*/
	private long amount;

	protected NumberConsumable(TriState requireFull, long amount) {
		super(requireFull);
		this.amount = amount;
	}

	@Override
	public boolean canConsumeFull(MinecraftServer server, UUID player, NbtCompound storage) {
		if (storage==null) {
			return canConsume(server, player, amount);
		}

		return storage.getBoolean(COMPLETE) || canConsume(server, player, amount-storage.getLong(AMOUNT_KEY));
	}

	@Override
	public void consume(MinecraftServer server, UUID player, NbtCompound storage) {
		if (storage.contains(COMPLETE)) return;

		var left = consume(server, player, amount-storage.getLong(AMOUNT_KEY));

		storage.putLong(AMOUNT_KEY, amount-left);
		if (left<=0) storage.putBoolean(COMPLETE, true);
	}

	public abstract boolean canConsume(MinecraftServer server, UUID player, long leftToConsume);
	/**
	 * Returns how much is left
	 */
	public abstract long consume(MinecraftServer server, UUID player, long leftToConsume);
}