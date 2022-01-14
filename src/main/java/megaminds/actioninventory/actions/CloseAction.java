package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.TypeName;
import net.minecraft.screen.slot.SlotActionType;

@TypeName("CloseGui")
public final class CloseAction extends BasicAction {
	private CloseAction() {}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		gui.close();
	}

	@Override
	public void validate() {
		//No validation needed.
	}
}