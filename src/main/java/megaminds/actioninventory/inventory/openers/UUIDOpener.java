package megaminds.actioninventory.inventory.openers;

import java.util.UUID;

import net.minecraft.entity.Entity;

public class UUIDOpener extends BaseOpener {
	private UUID uuid;

	@Override
	public boolean canOpen(Object o, ClickType click, What what) {
		if (!isValidWhat(what)||!isValidClick(click)) return false;

		if (getWhat()==What.Entity && o instanceof Entity) {
			return isValidClick(click) && ((Entity)o).getUuid().equals(uuid);
		}
		return false;
	}
}