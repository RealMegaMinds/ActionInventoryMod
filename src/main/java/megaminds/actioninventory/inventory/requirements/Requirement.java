package megaminds.actioninventory.inventory.requirements;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * A {@link Requirement} may be a cost or just a permission check
 */
public interface Requirement {
	public enum When {VIEW, CLICK}
	/**
	 * This method checks if the player has paid and if not, attempts to take payment.
	 * @return
	 * True - the player has paid and can view/use<br>
	 * False - the player has not paid
	 */
	public boolean pay(ServerPlayerEntity player);
	/**
	 * This is called after a player has successfully viewed/used.<br>
	 * {@link Requirement}s that require payment every time should reset the player's payment here.
	 */
	public void afterSuccess(ServerPlayerEntity player);
	
	public When getWhen();
}