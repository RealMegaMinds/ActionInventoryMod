package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.ScreenProperty;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.TypeName;
import net.minecraft.screen.slot.SlotActionType;

@TypeName("SendProperty")
public final class SendPropertyAction extends BasicAction {
	private ScreenProperty property;
	private int value;

	private SendPropertyAction() {}
	
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		if (property==null) property = ScreenProperty.FIRE_LEVEL;
		gui.sendProperty(property, value);
	}
}