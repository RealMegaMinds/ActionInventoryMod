package megaminds.actioninventory.callbacks.click.requirement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.Saver;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;

public class RequirementStore extends Saver {	
	private UUID owner;
	private NbtCompound stores;

	public RequirementStore(UUID uuid) {
		this.owner = uuid;
		this.stores = new NbtCompound();
		Saver.load(this);
	}

	public NbtCompound getOrCreate(String guiName) {
		if (stores==null) stores = new NbtCompound();

		if (!stores.contains(guiName)) {
			stores.put(guiName, new NbtCompound());
		}
		return stores.getCompound(guiName);		
	}

	public NbtCompound getOrCreateSub(String guiName, int slot) {
		NbtCompound nbt = getOrCreate(guiName);
		if (!nbt.contains("slot:"+slot)) {
			nbt.put("slot:"+slot, new NbtCompound());
		}
		return nbt.getCompound("slot:"+slot);
	}

	@Override
	public void load(Path loadDir) {
		try {
			File file = loadDir.resolve(owner.toString()+".dat").toFile();
			if (file.exists() && file.isFile()) {
				Helper.ifNotNullDo(NbtIo.readCompressed(file), c->stores=c);
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
			NbtIo.writeCompressed(stores, newFile);

			File current = saveDir.resolve(owner.toString() + ".dat").toFile();
			File backup = saveDir.resolve(owner.toString() + ".dat_old").toFile();
			Util.backupAndReplace(current, newFile, backup);
		} catch (IOException nbtCompound) {
			ActionInventoryMod.warn("Failed to save RequirementStore for: "+owner);
		}
	}
}