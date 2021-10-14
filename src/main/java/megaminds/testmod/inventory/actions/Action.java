package megaminds.testmod.inventory.actions;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Actions represent things done when an item is clicked in an ActionInventory
 */
public interface Action {
	void execute(ServerPlayerEntity player);
}