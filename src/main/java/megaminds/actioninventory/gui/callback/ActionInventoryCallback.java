package megaminds.actioninventory.gui.callback;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.gui.BetterGuiI;
import net.minecraft.screen.slot.SlotActionType;

public interface ActionInventoryCallback extends BetterClickCallback {
	@Override
	default boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		if (gui instanceof ActionInventoryGui g) {
			return cancellingClick(index, type, action, g);
		}
		return false;
	}
	
	boolean cancellingClick(int index, ClickType type, SlotActionType action, ActionInventoryGui gui);
}