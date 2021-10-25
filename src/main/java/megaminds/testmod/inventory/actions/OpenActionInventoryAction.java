package megaminds.testmod.inventory.actions;

import megaminds.testmod.inventory.ActionInventory;
import megaminds.testmod.inventory.ActionManager;
import megaminds.testmod.inventory.openers.Opener.What;
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