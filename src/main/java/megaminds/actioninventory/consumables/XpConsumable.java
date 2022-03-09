package megaminds.actioninventory.consumables;

import static megaminds.actioninventory.misc.Enums.COMPLETE;

import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
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

	public XpConsumable(TriState requireFull, int level, int amount) {
		super(requireFull);
		this.level = level;
		this.amount = amount;
	}

	@Override
	public boolean canConsumeFull(MinecraftServer server, UUID player, @Nullable NbtCompound storage) {
		var levelLeft = this.level;
		var amountLeft = this.amount;

		if (storage!=null) {
			if (storage.getBoolean(COMPLETE)) return true;

			levelLeft -= storage.getInt(LEVEL_KEY);
			amountLeft -= storage.getInt(AMOUNT_KEY);
		}

		var p = server.getPlayerManager().getPlayer(player);
		Objects.requireNonNull(p, ()->"No Player Exists for UUID: "+player);

		var extra = p.experienceLevel-levelLeft;
		if (extra<0) return false;
		return Helper.getTotalExperienceForLevel(extra) >= amountLeft;
	}

	@Override
	public void consume(MinecraftServer server, UUID player, @NotNull NbtCompound storage) {
		if (storage.getBoolean(COMPLETE)) return;

		var p = server.getPlayerManager().getPlayer(player);
		Objects.requireNonNull(p, ()->"No Player Exists for UUID: "+player);

		var levelLeft = this.level - storage.getInt(LEVEL_KEY);
		var amountLeft = this.amount - storage.getInt(AMOUNT_KEY);

		var extra = p.experienceLevel - levelLeft;
		if (isRequireFull().orElse(false)) {
			if (extra<0) return;
			if (Helper.getTotalExperienceForLevel(extra) >= amountLeft) {
				p.addExperienceLevels(-levelLeft);
				levelLeft = 0;
				p.addExperience(-amountLeft);
				amountLeft = 0;
			}
		} else {
			if (extra<0) {
				levelLeft -= p.experienceLevel;
				p.addExperienceLevels(-p.experienceLevel);
			} else {
				p.addExperienceLevels(-levelLeft);
				levelLeft = 0;
				if (Helper.getTotalExperienceForLevel(p.experienceLevel) >= amountLeft) {
					p.addExperience(-amountLeft);
					amountLeft = 0;
				} else {
					amountLeft -= Helper.getTotalExperienceForLevel(p.experienceLevel);
					p.addExperience(-Helper.getTotalExperienceForLevel(p.experienceLevel));
				}
			}
		}

		storage.putInt(LEVEL_KEY, level-levelLeft);
		storage.putInt(AMOUNT_KEY, amount-amountLeft);
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
		return new XpConsumable(isRequireFull(), level, amount);
	}
}