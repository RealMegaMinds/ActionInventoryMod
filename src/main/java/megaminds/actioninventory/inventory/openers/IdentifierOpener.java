package megaminds.actioninventory.inventory.openers;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class IdentifierOpener extends BaseOpener {
	private Identifier id;
	
	@Override
	public boolean canOpen(Object o, ClickType click, What what) {
		if (!isValidWhat(what)||!isValidClick(click)) return false;

		if (getWhat()==What.Entity && o instanceof Entity) {
			return Registry.ENTITY_TYPE.getId(((Entity)o).getType()).equals(id);
		} else if (getWhat()==What.Block && o instanceof Block) {
			return Registry.BLOCK.getId((Block)o).equals(id);
		} else if (getWhat()==What.BlockEntity && o instanceof BlockEntityType<?>) {
			return Registry.BLOCK_ENTITY_TYPE.getId((BlockEntityType<?>)o).equals(id);
		} else if (getWhat()==What.ItemStack && o instanceof ItemStack) {
			return Registry.ITEM.getId(((ItemStack)o).getItem()).equals(id);
		}
		return false;
	}
}