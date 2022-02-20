package megaminds.actioninventory.misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.consumables.BasicConsumable;
import megaminds.actioninventory.util.Helper;
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
	 * Returns the {@link NbtElement} for {@link BasicConsumable}s in the slot in the gui.
	 */
	public NbtElement getSub(String guiName, String lastAction) {
		if (stores==null) return null;
		NbtCompound c = stores.get(guiName);
		if (c==null) return null;
		return c.get(lastAction);
	}
	
	/**
	 * Returns the {@link NbtElement} (or gets from Supplier) for {@link BasicConsumable}s in the slot in the gui.
	 */
	public NbtElement getOrCreateSub(String guiName, String lastAction, Supplier<NbtElement> creator) {
		if (stores==null) stores = new HashMap<>();
		NbtCompound c = stores.computeIfAbsent(guiName, n->new NbtCompound());
		NbtElement e = c.get(lastAction);
		if (e==null) {
			e = creator.get();
			c.put(lastAction, e);
		}
    	return e;
	}
	
	/**
	 * Sets the element for the slot in the gui to the given one.
	 */
	public void setSub(String guiName, String lastAction, NbtElement el) {
		if (el==null) {
			removeSub(guiName, lastAction);
			return;
		}
		if (stores==null) stores = new HashMap<>();
		stores.computeIfAbsent(guiName, n->new NbtCompound()).put(lastAction, el);
	}
	
	public void removeSub(String guiName, String lastAction) {
		NbtCompound c;
		if (stores!=null && (c=stores.get(guiName))!=null) {
			c.remove(lastAction);
		}
	}
	
	/**
	 * Returns the {@link NbtElement} for the {@link BasicConsumable} in the slot in the gui.
	 */
	public NbtElement getDeepSub(String guiName, String lastAction, String consumable) {
		return ((NbtCompound)getOrCreateSub(guiName, lastAction, NbtCompound::new)).get(consumable);
	}

	/**
	 * Sets the {@link NbtElement} for the {@link BasicConsumable} in the slot in the gui.
	 */
	public void setDeepSub(String guiName, String lastAction, String consumable, NbtElement el) {
		((NbtCompound)getOrCreateSub(guiName, lastAction, NbtCompound::new)).put(consumable, el);
	}

	@Override
	public void load(Path loadDir) {
		try {
			File file = loadDir.resolve(owner.toString()+".dat").toFile();
			if (file.exists() && file.isFile()) {	
				stores = Helper.compoundToMap(NbtIo.readCompressed(file));
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
			NbtCompound compound = Helper.mapToCompound(stores);
			NbtIo.writeCompressed(compound, newFile);
			File current = saveDir.resolve(owner.toString() + ".dat").toFile();
			File backup = saveDir.resolve(owner.toString() + ".dat_old").toFile();
			Util.backupAndReplace(current, newFile, backup);
		} catch (IOException nbtCompound) {
			ActionInventoryMod.warn("Failed to save RequirementStore for: "+owner);
		}
	}
}