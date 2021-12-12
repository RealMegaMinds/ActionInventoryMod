package megaminds.actioninventory.api.actionobjects;

import megaminds.actioninventory.api.util.ObjectId;
import megaminds.actioninventory.api.util.PlayerData;
import net.minecraft.text.Text;

/**
 * A base for Requirements that check things.
 */
public abstract class Requirement extends ActionObject {
	public Requirement() {
	}

	public Requirement(ObjectId id, Text display) {
		super(id, display);
	}

	/**
	 * Returns the number that is the max progress/shows completion.
	 */
	public abstract int getMaxProgress();

	/**
	 * Resets the given player's progress for this requirement.
	 */
	public void reset(PlayerData player) {
		player.setProgress(getId(), 0);
	}

	/**
	 * Forcibly completes a player's progress for this requirement.
	 */
	public void forceComplete(PlayerData player) {
		player.setProgress(getId(), getMaxProgress());
	}

	/**
	 * Checks if the given player has completed this requirement.
	 */
	public boolean isCompleted(PlayerData player) {
		return player.getProgress(getId())==getMaxProgress();
	}
}