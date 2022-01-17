package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.screen.slot.SlotActionType;

@FunctionalInterface
public interface NamedGuiCallback extends ClickCallback {
	@Override
	default void click(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		if (gui instanceof NamedSlotGuiInterface ngui) {
			click(index, type, action, ngui);
		} else {
			throw new UnsupportedOperationException("SlotGuiInterface for a NamedGuiCallback must be an instance of NamedSlotGuiInterface!");
		}
	}
	void click(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui);
}