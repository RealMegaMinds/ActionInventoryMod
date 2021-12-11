package megaminds.actioninventory.api.actionobjects;

import megaminds.actioninventory.api.helper.PlayerData;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class Requirement extends ActionObject {
	public Requirement() {
	}

	public Requirement(Identifier id, Text display) {
		super(id, display);
	}

	public abstract int getMaxProgress();

	public void reset(PlayerData player) {
		player.setProgress(getFullId(), 0);
	}

	public void forceComplete(PlayerData player) {
		player.setProgress(getFullId(), getMaxProgress());
	}

	public boolean isCompleted(PlayerData player) {
		return player.getProgress(getFullId())==getMaxProgress();
	}
}