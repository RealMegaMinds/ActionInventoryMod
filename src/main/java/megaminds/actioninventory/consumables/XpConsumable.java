package megaminds.actioninventory.consumables;

import static megaminds.actioninventory.misc.Enums.COMPLETE;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.misc.Enums;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

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

	@Override
	public boolean canConsumeFull(MinecraftServer server, UUID player, @Nullable NbtCompound storage) {
		if (Helper.getBoolean(storage, Enums.COMPLETE)) return true;

		var left = getLeft(storage);
		var p = Helper.getPlayer(server, player);

		var extra = p.experienceLevel-left[0];
		if (extra<0) return false;
		return Helper.getTotalExperienceForLevel(extra)+p.experienceProgress*p.getNextLevelExperience() >= left[1];
	}

	@Override
	public boolean consume(MinecraftServer server, UUID player, @NotNull NbtCompound storage) {
		if (storage.getBoolean(Enums.COMPLETE)) return true;

		var p = Helper.getPlayer(server, player);

		var left = getLeft(storage);
		var extra = p.experienceLevel - left[0];
		if (extra<0) {
			left[0] -= p.experienceLevel;
			p.addExperienceLevels(-p.experienceLevel);
		} else {
			p.addExperienceLevels(-left[0]);
			left[0] = 0;

			if (Helper.getTotalExperienceForLevel(p.experienceLevel) >= left[1]) {
				p.addExperience(-left[1]);
				left[1] = 0;
			} else {
				left[1] -= Helper.getTotalExperienceForLevel(p.experienceLevel);
				p.addExperience(-Helper.getTotalExperienceForLevel(p.experienceLevel));
			}
		}

		if (left[0]<=0 && left[1]<=0) {
			storage.remove(LEVEL_KEY);
			storage.remove(AMOUNT_KEY);
			storage.putBoolean(COMPLETE, true);
			return true;
		} else {
			storage.putInt(LEVEL_KEY, level-left[0]);
			storage.putInt(AMOUNT_KEY, amount-left[1]);
			return false;
		}
	}

	/**
	 * Returns an array of form [levelLeft, amountLeft]
	 */
	private int[] getLeft(@Nullable NbtCompound storage) {
		if (Helper.getBoolean(storage, COMPLETE)) return new int[2];
		return new int[] {level-Helper.getInt(storage, LEVEL_KEY), amount-Helper.getInt(storage, AMOUNT_KEY)};
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
		return new XpConsumable(level, amount);
	}
}