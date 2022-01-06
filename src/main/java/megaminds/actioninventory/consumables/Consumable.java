package megaminds.actioninventory.consumables;

import java.util.function.Supplier;

public enum Consumable {
	XP(XpConsumable::new);
	
	private final Supplier<BasicConsumable> supplier;
	
	private Consumable(Supplier<BasicConsumable> supplier) {
		this.supplier = supplier;
	}
	
	public BasicConsumable get() {
		return supplier.get();
	}
}