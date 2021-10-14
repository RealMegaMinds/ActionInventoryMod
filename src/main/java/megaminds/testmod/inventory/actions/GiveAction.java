package megaminds.testmod.inventory.actions;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This gives an item to the player (will be dropped if the player's inventory is full)
 */
public class GiveAction implements Action {
	private final ItemStack itemStack;
	
	/**
	 * @param itemToGive
	 * The item to give the player
	 */
	public GiveAction(ItemStack itemToGive) {
		this.itemStack = itemToGive.copy();
	}

	@Override
	public void execute(ServerPlayerEntity player) {
		player.getInventory().offerOrDrop(itemStack.copy());
	}
}