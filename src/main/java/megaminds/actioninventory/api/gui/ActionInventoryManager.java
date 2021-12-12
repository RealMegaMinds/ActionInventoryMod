package megaminds.actioninventory.api.gui;

import java.util.Set;

import megaminds.actioninventory.api.base.ActionInventory;
import megaminds.actioninventory.api.base.ActionObjectHandler;
import megaminds.actioninventory.api.util.ObjectId;
import net.minecraft.util.Identifier;

/**
 * This manages all ActionInventories for the server.
 */
public class ActionInventoryManager {
	/**
	 * Returns the ActionInventory with the given id.<br>
	 * Returns null if there is no ActionInventory with the given id.
	 */
	public static ActionInventory getInventory(Identifier id) {
		return (ActionInventory) ActionObjectHandler.getActionObject(new ObjectId(ActionInventory.TYPE, id));
	}

	/**
	 * Returns a stream of the ids of all the ActionInventories.
	 */
	public static Set<Identifier> getAllInventoryIds() {
		return ActionObjectHandler.getAllOfType(ActionInventory.TYPE);
	}

	/**
	 * This actually opens an inventory and doesn't perform any checks.
	 */
	//	public static <E extends ActionInventory> void open(@NotNull ActionInventory inventory, ServerPlayerEntity player) {
	//		player.openHandledScreen(inventory);
	//	}
}