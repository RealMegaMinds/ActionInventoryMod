package megaminds.actioninventory.callbacks.click;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This gives an item to the player (will be dropped if the player's inventory is full)
 */
public class GiveAction implements Action {
	/** The item to give the player*/
	private ItemStack itemStack;
	
	@Override
	public void execute(ServerPlayerEntity player) {
		player.getInventory().offerOrDrop(itemStack.copy());
	}
}