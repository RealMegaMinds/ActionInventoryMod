package megaminds.actioninventory.inventory.requirements;

import megaminds.actioninventory.inventory.Typed;
import megaminds.actioninventory.inventory.requirements.Requirement.Type;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * A {@link Requirement} may be a cost or just a permission check
 */
public abstract class Requirement extends Typed<Type> {
	public enum Type {
		XP(XPRequirement.class);
		
		public final Class<? extends Requirement> clazz;
		private Type(Class<? extends Requirement> clazz) {
			this.clazz = clazz;
		}
	}
	public enum When {VIEW, CLICK}
	
	private When when;

	public When getWhen() {
		return when;
	}
	/**
	 * This method checks if the player has paid and if not, attempts to take payment.
	 * @return
	 * True - the player has paid and can view/use<br>
	 * False - the player has not paid
	 */
	public abstract boolean pay(ServerPlayerEntity player);
	/**
	 * This is called after a player has successfully viewed/used.<br>
	 * {@link Requirement}s that require payment every time should reset the player's payment here.
	 */
	public abstract void afterSuccess(ServerPlayerEntity player);
}