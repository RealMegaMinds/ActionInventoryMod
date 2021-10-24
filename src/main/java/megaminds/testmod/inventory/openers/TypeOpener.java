package megaminds.testmod.inventory.openers;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public class TypeOpener extends Opener {
	private Identifier type;

	@Override
	public boolean canOpen(Object o, ClickType click, What what) {
		if (!isValidWhat(what)||!isValidClick(click)) return false;

		if (getWhat()==What.Entity && o instanceof Entity) {
			return EntityType.getId(((Entity)o).getType()).equals(type);
		} else if (getWhat()==What.BlockEntity && o instanceof BlockEntity) {
			return BlockEntityType.getId(((BlockEntity)o).getType()).equals(type);
		}
		return false;
	}

	@Override
	protected Type getTypeInternal() {
		return Type.TYPE;
	}
}