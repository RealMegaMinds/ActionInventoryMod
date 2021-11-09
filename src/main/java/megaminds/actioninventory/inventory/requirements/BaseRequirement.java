package megaminds.actioninventory.inventory.requirements;

public abstract class BaseRequirement implements Requirement {
	private When when;

	public When getWhen() {
		return when;
	}
}