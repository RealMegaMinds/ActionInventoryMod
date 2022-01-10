package megaminds.actioninventory.consumables;

import megaminds.actioninventory.util.TypeName;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;

@TypeName("Xp")
public class XpConsumable extends BasicConsumable {
	private static final String LEVEL = "level", AMOUNT = "amount";
	
	private int level, amount;

	@Override
	public boolean canConsumeFull(ServerPlayerEntity player, NbtElement storage) {
		if (NbtByte.ONE.equals(storage)) return true;
		int[] saved = {player.experienceLevel, player.totalExperience};
		if (storage!=null) {
			NbtCompound c = (NbtCompound) storage;
			saved[0] += c.contains(LEVEL) ? c.getInt(LEVEL) : 0;
			saved[1] += c.contains(AMOUNT) ? c.getInt(AMOUNT) : 0;
		}
		return (saved[0] >= level) && (saved[1] >= amount);
	}

	@Override
	public NbtElement consume(ServerPlayerEntity player, NbtElement storage) {
		if (NbtByte.ONE.equals(storage)) return storage;

		NbtCompound c = storage!=null ? (NbtCompound) storage : new NbtCompound();
		int[] paid = {
				c.contains(LEVEL) ? c.getInt(LEVEL) : 0,
				c.contains(AMOUNT) ? c.getInt(AMOUNT) : 0
		};

		int[] toPay = {
				Math.min(level-paid[0], player.experienceLevel),
				Math.min(amount-paid[1], player.totalExperience)
		};
		
		player.addExperienceLevels(-toPay[0]);
		player.addExperience(-toPay[1]);
		paid[0] += toPay[0];
		paid[1] += toPay[1];
		
		if (paid[0]>=level && paid[1]>=amount) {
			return NbtByte.ONE;
		}
		c.putInt(LEVEL, level);
		c.putInt(AMOUNT, amount);
		return c;
	}

	@Override
	public String getStorageName() {
		return "Xp";
	}
}