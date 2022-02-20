package megaminds.actioninventory.gui.callback;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.screen.slot.SlotActionType;

public interface CancellableCallback extends ClickCallback {
	@Override
	default void click(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		cancellingClick(index, type, action, gui);
	}
	
	/**
	 * Returns true if further callbacks should be cancelled.
	 */
	boolean cancellingClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui);
}