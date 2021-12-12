package megaminds.actioninventory.api.actionobjects;

import megaminds.actioninventory.api.helper.ObjectId;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * A base for creating Actions that do things.
 */
public abstract class Action extends ActionObject {
	public Action() {
	}

	public Action(ObjectId id, Text display) {
		super(id, display);
	}

	/**
	 * Executes this action based on the given player.
	 */
	public abstract void execute(ServerPlayerEntity player);
}