package megaminds.actioninventory.actions;

import java.util.function.Consumer;

import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.annotations.Instanced;
import megaminds.actioninventory.util.annotations.PolyName;

@PolyName("Empty")
@Instanced
public non-sealed class EmptyAction extends BasicAction {
	public static final EmptyAction INSTANCE = new EmptyAction();
	
	private EmptyAction() {}
	
	@Override
	public void validate() {
		//Unused
	}
	
	@Override
	public void accept(ActionInventoryGui gui) {
		//Unused
	}

	@Override
	public BasicAction copy() {
		return INSTANCE;
	}
	
	public static EmptyAction getNew(Consumer<ActionInventoryGui> consumer) {
		return new EmptyAction() {
			@Override
			public void accept(ActionInventoryGui gui) {
				consumer.accept(gui);
			}
		};
	}
}