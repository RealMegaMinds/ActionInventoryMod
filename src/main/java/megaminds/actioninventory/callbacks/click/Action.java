package megaminds.actioninventory.callbacks.click;

import java.util.function.Supplier;

import megaminds.actioninventory.callbacks.click.requirement.XpRequirement;

public enum Action {
	COMMAND(CommandAction::new), GIVE(GiveAction::new), MESSAGE(MessageAction::new), SOUND(SoundAction::new), CLOSE(CloseAction::new), PROPERTY(SendPropertyAction::new), REQUIRE_XP(XpRequirement::new);
	
	private final Supplier<BasicAction> supplier;
	
	private Action(Supplier<BasicAction> supplier) {
		this.supplier = supplier;
	}
	
	public BasicAction get() {
		return supplier.get();
	}
}