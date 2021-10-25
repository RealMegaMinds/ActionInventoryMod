package megaminds.actioninventory.inventory.actions;

import megaminds.actioninventory.inventory.ActionInventory;
import megaminds.actioninventory.inventory.ActionManager;
import megaminds.actioninventory.inventory.openers.Opener.What;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This opens an {@link ActionInventory} screen
 */
public class OpenActionInventoryAction extends Action {
	/** The name of the {@link ActionInventory}*/
	private String name;
	
	@Override
	public void execute(ServerPlayerEntity player) {
		ActionManager.notify(ActionManager.getInventory(name), player, null, What.InventoryClick, null);
	}

	@Override
	protected Type getTypeInternal() {
		return Type.Open;
	}
}