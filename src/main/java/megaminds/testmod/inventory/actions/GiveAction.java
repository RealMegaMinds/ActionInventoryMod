package megaminds.testmod.inventory.actions;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This gives an item to the player (will be dropped if the player's inventory is full)
 */
public class GiveAction extends Action {
	/** The item to give the player*/
	private ItemStack itemStack;
	
	@Override
	public void execute(ServerPlayerEntity player) {
		player.getInventory().offerOrDrop(itemStack.copy());
	}

	@Override
	protected Type getTypeInternal() {
		return Type.Give;
	}
}