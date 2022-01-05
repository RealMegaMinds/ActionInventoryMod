package megaminds.actioninventory.actions;

import java.util.function.Supplier;

public enum Action {
	OPEN_GUI(ChangeGuiAction::new),
	CLOSE(CloseAction::new),
	COMMAND(CommandAction::new),
	GIVE(GiveAction::new),
	MESSAGE(MessageAction::new),
	PROPERTY(SendPropertyAction::new),
	SOUND(SoundAction::new),
	REQUIRE(RequirementAction::new),
	CONSUME(ConsumeAction::new);
	
	private final Supplier<BasicAction> supplier;
	
	private Action(Supplier<BasicAction> supplier) {
		this.supplier = supplier;
	}
	
	public BasicAction get() {
		return supplier.get();
	}
}