package megaminds.testmod.inventory.openers;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class NbtOpener extends Opener {
	private String key;
	private NbtElement value;
	private boolean onlyCheckPresence;

	@Override
	public boolean canOpen(Object o, ClickType click, What what) {
		if (!isValidWhat(what)||!isValidClick(click)) return false;

		NbtCompound data = new NbtCompound();
		if (getWhat()==What.BlockEntity && o instanceof BlockEntity) {
			((BlockEntity)o).writeNbt(data);
		} else if (getWhat()==What.Entity && o instanceof Entity) {
			((Entity)o).writeNbt(data);
		} else if (getWhat()==What.ItemStack && o instanceof ItemStack) {
			((ItemStack)o).writeNbt(data);
		}
		
		if (data.isEmpty()) {
			return false;
		}
		
		if (onlyCheckPresence) {
			return data.contains(key);
		} else {
			return data.contains(key) && data.get(key).equals(value);
		}
	}

	@Override
	protected Type getTypeInternal() {
		return Type.NBT;
	}
}