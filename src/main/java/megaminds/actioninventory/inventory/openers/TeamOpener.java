package megaminds.actioninventory.inventory.openers;

import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.AbstractTeam;

public class TeamOpener extends Opener {
	private String teamName;

	@Override
	public boolean canOpen(Object o, ClickType click, What what) {
		if (!isValidWhat(what)||!isValidClick(click)) return false;

		if (getWhat()==What.Entity && o instanceof Entity) {
			AbstractTeam team = ((Entity)o).getScoreboardTeam();
			return isValidClick(click) && team!=null && team.getName().equals(teamName);
		}
		return false;
	}

	@Override
	protected Type getTypeInternal() {
		return Type.TEAM;
	}
}