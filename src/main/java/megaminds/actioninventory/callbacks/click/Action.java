package megaminds.actioninventory.callbacks.click;

import java.util.function.Supplier;

public enum Action {
	COMMAND(CommandAction::new), GIVE(GiveAction::new), MESSAGE(MessageAction::new), SOUND(SoundAction::new), CLOSE(CloseAction::new), PROPERTY(SendPropertyAction::new);
	
	private final Supplier<BasicAction> supplier;
	
	private Action(Supplier<BasicAction> supplier) {
		this.supplier = supplier;
	}
	
	public BasicAction get() {
		return supplier.get();
	}
}