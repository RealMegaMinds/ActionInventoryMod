package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.TypeName;
import net.minecraft.screen.slot.SlotActionType;

@TypeName("CloseGui")
public final class CloseAction extends BasicAction {
	public CloseAction() {
	}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		gui.close();
	}
}