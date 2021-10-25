package megaminds.actioninventory.inventory.openers;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class NameOpener extends Opener {
	private MutableText name;
	private boolean onlyChecksText;
	private boolean useCustom;

	@Override
	public boolean canOpen(Object o, ClickType click, What what) {
		if (!isValidWhat(what)||!isValidClick(click)) return false;
		
		Text t = null;
		if (getWhat()==What.Entity && o instanceof Entity) {
			Entity e = (Entity)o;
			if (useCustom==e.hasCustomName()) {
				t = e.getName();
			}
		} else if (getWhat()==What.ItemStack && o instanceof ItemStack) {
			ItemStack stack = (ItemStack)o;
			if (useCustom==stack.hasCustomName()) {
				t = stack.getName();
			}
		} else if (!useCustom && getWhat()==What.Block && o instanceof Block) {
			t = ((Block)o).getName();
		}
		
		if (t==null) {
			return false;
		}

		if (onlyChecksText) {
			return name.getString().equals(t.getString());
		} else {
			return name.equals(t);
		}
	}

	@Override
	protected Type getTypeInternal() {
		return Type.NAME;
	}
}