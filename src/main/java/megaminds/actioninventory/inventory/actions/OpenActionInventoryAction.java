package megaminds.actioninventory.inventory.actions;

import megaminds.actioninventory.inventory.ActionInventory;
import megaminds.actioninventory.inventory.helpers.ActionManager;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This opens an {@link ActionInventory} screen
 */
public class OpenActionInventoryAction extends Action {
	/** The name of the {@link ActionInventory}*/
	private String name;
	
	@Override
	public void execute(ServerPlayerEntity player) {
		ActionManager.onAction(player, name);
	}

	@Override
	protected Type getTypeInternal() {
		return Type.Open;
	}
}