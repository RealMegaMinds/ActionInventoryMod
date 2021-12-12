package megaminds.actioninventory.api.util;

import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NbtCompound;

/**
 * Contains all the stuff a player has done with ActionInventories.
 */
public class PlayerData {
	private UUID player;
	private Object2IntMap<ObjectId> data;
	private boolean isDirty;
	
	public PlayerData(UUID player) {
		this.player = player;
		this.data = new Object2IntOpenHashMap<>();
	}
	
	/**
	 * Returns the player whose data this is.
	 */
	public UUID getPlayer() {
		return player;
	}
	
	/**
	 * Returns true if this PlayerData needs to be saved.
	 */
	public boolean isDirty() {
		return isDirty;
	}
	
	/**
	 * Returns this players progress on the specified object.
	 */
	public int getProgress(ObjectId id) {
		return data.getInt(id);
	}
	
	/**
	 * Changes this player's progress based on given completion data.
	 */
	public void setProgress(ObjectId id, int progress) {
		if (progress==0) {
			if (data.containsKey(id)) markDirty();
			data.removeInt(id);
		} else {
			data.put(id, progress);
			markDirty();
		}
	}
	
	/**
	 * Clears all of this player's progress for everything.
	 */
	public void resetAllProgress() {
		if (!data.isEmpty()) {
			markDirty();
		}
		data.clear();
	}
	
	private void markDirty() {
		isDirty = true;
	}
	
	/**
	 * Serializes this PlayerData to an NbtCompound
	 */
	public NbtCompound serializeNbt() {
		NbtCompound compound = new NbtCompound();
		for (Entry<ObjectId> e : data.object2IntEntrySet()) {
			compound.putInt(e.getKey().toString(), e.getIntValue());
		}
		return compound;
	}
	
	/**
	 * Deserializes this PlayerData from an NbtCompound.
	 */
	public void deserializeNbt(NbtCompound compound) {
		data.clear();
		
		String[] keys = compound.getKeys().toArray(String[]::new);
		for (String s : keys) {
			data.put(ObjectId.fromString(s), compound.getInt(s));
		}
	}
}