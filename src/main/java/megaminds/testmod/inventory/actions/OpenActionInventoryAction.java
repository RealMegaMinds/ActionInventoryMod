package megaminds.testmod.inventory.actions;

import megaminds.testmod.MessageHelper;
import megaminds.testmod.inventory.ActionInventory;
import megaminds.testmod.inventory.ActionInventoryManager;
import megaminds.testmod.inventory.OpenRequirement;
import megaminds.testmod.inventory.OpenRequirement.OpenType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This opens an {@link ActionInventory} screen
 */
public class OpenActionInventoryAction implements Action {
	private final String name;
	
	/**
	 * @param nameOfInventory
	 * The name of the {@link ActionInventory}
	 */
	public OpenActionInventoryAction(String nameOfInventory) {
		this.name = nameOfInventory;
	}

	@Override
	public void execute(ServerPlayerEntity player) {
		ActionInventory inv = ActionInventoryManager.getInventory(name);
		if (inv==null) {
			MessageHelper.toPlayerMessage(player, MessageHelper.toError("No Action Inventory Called: "+name), true);
		} else if (OpenRequirement.check(inv.getOpenRequirement(), OpenType.INV_CLICK, null, null)) {
			ActionInventoryManager.open(inv, player);
		} else {
			MessageHelper.toPlayerMessage(player, MessageHelper.toError("Cannot Open Action Inventory Called: "+name), true);
		}
	}
}