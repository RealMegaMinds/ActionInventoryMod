package megaminds.actioninventory.inventory.openers;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

public class TagOpener extends Opener {
	private Identifier tag;

	@Override
	public boolean canOpen(Object o, ClickType click, What what) {
		if (!isValidWhat(what)||!isValidClick(click)) return false;

		if (getWhat()==What.Block && o instanceof Block) {
			return BlockTags.getTagGroup().getTagsFor((Block)o).contains(tag);
		} else if (getWhat()==What.ItemStack && o instanceof ItemStack) {
			return ItemTags.getTagGroup().getTagsFor(((ItemStack)o).getItem()).contains(tag);
		} else if (getWhat()==What.Entity && o instanceof Entity) {
			return EntityTypeTags.getTagGroup().getTagsFor(((Entity)o).getType()).contains(o);
		}
		return false;
	}

	@Override
	protected Type getTypeInternal() {
		return Type.TAG;
	}
}