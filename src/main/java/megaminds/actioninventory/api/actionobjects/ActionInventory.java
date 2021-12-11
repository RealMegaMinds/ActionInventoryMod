package megaminds.actioninventory.api.actionobjects;

import java.util.List;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.Helper;
import megaminds.actioninventory.api.helper.ObjectId;
import net.minecraft.util.Identifier;

public class ActionInventory extends ActionObject {
	public static final Identifier TYPE =  new Identifier(ActionInventoryMod.MOD_ID, "ActionInventory");

	private List<ActionItem> items;
	private Requirement requirement;

	@Override
	protected Identifier getType() {
		return TYPE;
	}
	@Override
	public void deleteChild(ObjectId child) {
		if (requirement!=null && child.equals(requirement.getFullId())) {
			requirement = null;
			return;
		}
		Helper.removeFirst(items, i->child.equals(i.getFullId()));
	}
	@Override
	public void deleteChildren() {
		items.clear();
		requirement = null;
	}
}