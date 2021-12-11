package megaminds.actioninventory.api.actionobjects;

import megaminds.actioninventory.api.helper.PlayerData;

public abstract class Action extends ActionObject {
	/**
	 * Executes this action.
	 */
	public abstract void execute(PlayerData player);
}