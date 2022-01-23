package megaminds.actioninventory.consumables;

import static megaminds.actioninventory.misc.Enums.COMPLETE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.server.network.ServerPlayerEntity;

/**@since 3.1*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract sealed class NumberConsumable extends BasicConsumable permits ItemConsumable {
	private long amount;
	
	@Override
	public boolean canConsumeFull(ServerPlayerEntity player, NbtElement storage) {
		return COMPLETE.equals(storage) 
				|| storage!=null && 
				storage instanceof NbtLong n 
				&& canConsume(player, getLeft(n.longValue())) //amount-paid left
				|| canConsume(player, amount);	//full amount left
	}

	@Override
	public NbtElement consume(ServerPlayerEntity player, NbtElement storage, boolean saveAmount) {
		if (COMPLETE.equals(storage)) return COMPLETE;
		
		long paid = (storage instanceof NbtLong l) ? l.longValue() : 0;
		if (paid>=amount) return COMPLETE;
		
		long left = consume(player, getLeft(paid));
		if (left<=0) return COMPLETE;
		
		return NbtLong.of(amount-left);
	}
	
	public long getLeft(long paid) {
		return amount-paid;
	}
	
	public abstract boolean canConsume(ServerPlayerEntity player, long leftToConsume);
	/**
	 * Returns how much is left
	 */
	public abstract long consume(ServerPlayerEntity player, long leftToConsume);
}