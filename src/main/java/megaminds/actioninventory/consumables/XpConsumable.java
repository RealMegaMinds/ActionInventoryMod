package megaminds.actioninventory.consumables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PolyName("Xp")
public final class XpConsumable extends BasicConsumable {
	private static final String LEVEL_KEY = "level";
	private static final String AMOUNT_KEY = "amount";
	
	private int level;
	private int amount;
	
	public XpConsumable(boolean requireFull, int level, int amount) {
		super(requireFull);
		this.level = level;
		this.amount = amount;
	}
	
	@Override
	public boolean canConsumeFull(ServerPlayerEntity player, NbtElement storage) {
		if (NbtByte.ONE.equals(storage)) return true;
		int[] saved = {player.experienceLevel, player.totalExperience};
		if (storage!=null) {
			NbtCompound c = (NbtCompound) storage;
			if (c.contains(LEVEL_KEY)) saved[0] += c.getInt(LEVEL_KEY);
			if (c.contains(AMOUNT_KEY)) saved[1] += c.getInt(AMOUNT_KEY);
		}
		return (saved[0] >= level) && (saved[1] >= amount);
	}

	@Override
	public NbtElement consume(ServerPlayerEntity player, NbtElement storage, boolean saveAmount) {
		if (NbtByte.ONE.equals(storage)) return storage;

		NbtCompound c = (NbtCompound) storage;
		if (c==null) c = new NbtCompound();
		
		int[] paid = {
				c.contains(LEVEL_KEY) ? c.getInt(LEVEL_KEY) : 0,
				c.contains(AMOUNT_KEY) ? c.getInt(AMOUNT_KEY) : 0
		};

		int[] toPay = {
				Math.min(level-paid[0], player.experienceLevel),
				Math.min(amount-paid[1], player.totalExperience)
		};
		
		player.addExperienceLevels(-toPay[0]);
		player.addExperience(-toPay[1]);
		paid[0] += toPay[0];
		paid[1] += toPay[1];
		
		if (!saveAmount) return null;
		
		if (paid[0]>=level && paid[1]>=amount) {
			return NbtByte.ONE;
		}

		c.putInt(LEVEL_KEY, paid[0]);
		c.putInt(AMOUNT_KEY, paid[1]);
		return c;
	}

	@Override
	public String getStorageName() {
		return "Xp";
	}

	@Override
	public void validate() {
		Validated.validate(level>=0, "Xp consumable requires level to be 0 or above, but it is: "+level);
		Validated.validate(amount>=0, "Xp consumable requires amount to be 0 or above, but it is: "+amount);
	}

	@Override
	public BasicConsumable copy() {
		return new XpConsumable(getRequireFull(), level, amount);
	}
}