package megaminds.actioninventory.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.consumables.BasicConsumable;
import megaminds.actioninventory.mixin.NbtCompoundMixin;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;

/**
 * Represents a file that acts as a storage for Consumables as they relate to a given player.<br>
 * File: "&lt;UUID&gt;.dat"<br>
 * Map<[guiName], Map<[slotIndex], NbtCompound>><br>
 */
public class StoredConsumables extends Saver {	
	private UUID owner;
	private Map<String, NbtCompound> stores;

	/**
	 * Creates a {@link StoredConsumables} for the given UUID. UUID most likely comes from a player.
	 */
	public StoredConsumables(UUID uuid) {
		this.owner = uuid;
		Saver.load(this);
	}

	/**
	 * Returns the {@link NbtElement} (or gets from Supplier) for {@link BasicConsumable}s in the slot in the gui.
	 */
	public NbtElement getOrCreateSub(String guiName, int slot, Supplier<NbtElement> creator) {
		NbtCompound c = Objects.requireNonNullElseGet(stores, ()->stores=new HashMap<>()).computeIfAbsent(guiName, n->new NbtCompound());
    	return Objects.requireNonNullElseGet(c.get(slot+""), ()->c.put(slot+"", creator.get()));
	}
	
	/**
	 * Sets the element for the slot in the gui to the given one.
	 */
	public void setSub(String guiName, int slot, NbtElement el) {
		if (el==null) return;
		if (stores==null) stores = new HashMap<>();
		Objects.requireNonNullElseGet(stores, ()->stores=new HashMap<>()).computeIfAbsent(guiName, n->new NbtCompound()).put(slot+"", el);
	}
	
	/**
	 * Returns the {@link NbtElement} for the {@link BasicConsumable} in the slot in the gui.
	 */
	public NbtElement getDeepSub(String guiName, int slot, String consumable) {
		return ((NbtCompound)getOrCreateSub(guiName, slot, NbtCompound::new)).get(consumable);
	}

	/**
	 * Sets the {@link NbtElement} for the {@link BasicConsumable} in the slot in the gui.
	 */
	public void setDeepSub(String guiName, int slot, String consumable, NbtElement el) {
		((NbtCompound)getOrCreateSub(guiName, slot, NbtCompound::new)).put(consumable, el);
	}

	@Override
	public void load(Path loadDir) {
		try {
			File file = loadDir.resolve(owner.toString()+".dat").toFile();
			if (file.exists() && file.isFile()) {
				stores = Helper.mapEach(((NbtCompoundMixin)NbtIo.readCompressed(file)).invokeToMap(), NbtCompound.class::cast, null);
			}
		} catch (IOException file) {
			ActionInventoryMod.warn("Failed to load RequirementStore for: "+owner);
		}
	}

	@Override
	public void save(Path saveDir) {
		if (stores==null) return;

		try {
			File newFile = Files.createTempFile(saveDir, owner.toString() + "-", ".dat").toFile();
			NbtCompound compound = Helper.createNbtCompound(stores);
			NbtIo.writeCompressed(compound, newFile);
			File current = saveDir.resolve(owner.toString() + ".dat").toFile();
			File backup = saveDir.resolve(owner.toString() + ".dat_old").toFile();
			Util.backupAndReplace(current, newFile, backup);
		} catch (IOException nbtCompound) {
			ActionInventoryMod.warn("Failed to save RequirementStore for: "+owner);
		}
	}
}