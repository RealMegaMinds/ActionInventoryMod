package megaminds.testmod.inventory.requirements;

import megaminds.testmod.inventory.InventoryItem;
import megaminds.testmod.inventory.storable.StoredRequirement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * A {@link Requirement} may be a cost or just a permission check
 */
public interface Requirement {
	/**
	 * @return
	 * The actual requirement that the player is checked against
	 */
	StoredRequirement getStoredRequirement();
	/**
	 * @return
	 * True - player has paid the whole cost
	 * False - player has not paid the whole cost (has paid partial or no cost)
	 */
	boolean hasPaidFull(ServerPlayerEntity player);
	/**
	 * @return
	 * True - this requirement accepts costs in partial amounts
	 * False - the cost must be paid all at once
	 */
	boolean acceptsPartialCost();
	/**
	 * @return
	 * True - player has the full cost
	 * False - player doesn't have the full cost
	 */
	boolean hasFullCost(ServerPlayerEntity player);
	/**
	 * Removes the cost from the player. (a permission check may do nothing)
	 * @return
	 * {@link Requirement#hasPaidFull}
	 */
	boolean takeCost(ServerPlayerEntity player);
	/**
	 * True - is requirement for viewing
	 * False - is requirement for clicking
	 */
	boolean isViewRequirement();
	/**
	 * Return the error for this requirement
	 */
	Text getError();
	/**
	 * Called after the {@link InventoryItem} has been successfully clicked. This could reset the costs or may do nothing.
	 */
	void afterClick(ServerPlayerEntity player);
	
	/**
	 * This is a convenience method for the player to pay partial or whole amount
	 * @param player
	 * @return
	 * {@link Requirement#hasPaidFull}
	 */
	default boolean tryPay(ServerPlayerEntity player) {
		if (hasPaidFull(player)) return true;
		
		if (acceptsPartialCost() || hasFullCost(player)) {
			return takeCost(player);
		}
		
		return false;
	}
}