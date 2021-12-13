package megaminds.actioninventory.callbacks.click;

import megaminds.actioninventory.inventory.ActionInventoryImpl;
import megaminds.actioninventory.inventory.helpers.ActionManager;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This opens an {@link ActionInventoryImpl} screen
 */
public class OpenActionInventoryAction implements Action {
	/** The name of the {@link ActionInventoryImpl}*/
	private String name;
	
	@Override
	public void execute(ServerPlayerEntity player) {
		ActionManager.onAction(player, name);
	}
}