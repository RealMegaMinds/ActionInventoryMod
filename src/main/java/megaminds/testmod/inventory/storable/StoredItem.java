package megaminds.testmod.inventory.storable;

import com.google.common.collect.ImmutableList;

import net.minecraft.nbt.NbtCompound;

public class StoredItem {
	public final byte slot;
	public final NbtCompound itemStack;
	public final ImmutableList<StoredRequirement> requirements;
	public final ImmutableList<StoredAction> actions;
	
	public StoredItem() {
		this.slot = 0;
		this.itemStack = null;
		this.requirements = null;
		this.actions = null;
	}
	
	public StoredItem(byte slot, NbtCompound itemStack, ImmutableList<StoredRequirement> requirements, ImmutableList<StoredAction> actions) {
		this.slot = slot;
		this.itemStack = itemStack;
		this.requirements = requirements;
		this.actions = actions;
	}
}