package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.ScreenProperty;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.TypeName;
import net.minecraft.screen.slot.SlotActionType;

@TypeName("SendProperty")
public final class SendPropertyAction extends BasicAction {
	private ScreenProperty property = ScreenProperty.FIRE_LEVEL;
	private int value;

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		gui.sendProperty(property, value);
	}
}