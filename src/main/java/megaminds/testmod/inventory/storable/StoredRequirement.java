package megaminds.testmod.inventory.storable;

public class StoredRequirement {
	public enum Type {XP}
	public enum When {VIEW, CLICK}
	public final int requiredCost;
	public final Type type;
	public final When when;
	public final boolean consumes;
	public final boolean allowPartial;
	
	public StoredRequirement() {
		this.requiredCost = -1;
		this.type = null;
		this.when = null;
		this.consumes = false;
		this.allowPartial = false;
	}

	public StoredRequirement(int requiredCost, Type type, boolean consumes, boolean allowPartial, When when) {
		this.requiredCost = requiredCost;
		this.type = type;
		this.when = when;
		this.consumes = consumes;
		this.allowPartial = allowPartial;
	}
}