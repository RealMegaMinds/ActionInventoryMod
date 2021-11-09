package megaminds.actioninventory.inventory.openers;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class PosOpener extends BaseOpener {
	private BlockPos pos;

	@Override
	public boolean canOpen(Object o, ClickType click, What what) {
		if (!isValidWhat(what)||!isValidClick(click)) return false;

		if (getWhat()==What.BlockEntity && o instanceof BlockEntity) {
			return ((BlockEntity)o).getPos().equals(pos);
		} else if (getWhat()==What.Entity && o instanceof Entity) {
			return ((Entity)o).getBlockPos().equals(pos);
		}
		return false;
	}
}