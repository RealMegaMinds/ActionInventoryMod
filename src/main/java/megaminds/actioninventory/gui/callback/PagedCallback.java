package megaminds.actioninventory.gui.callback;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.BetterGuiI;
import megaminds.actioninventory.gui.PagedGui;
import net.minecraft.screen.slot.SlotActionType;

public interface PagedCallback extends BetterClickCallback {
	default boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		if (gui instanceof PagedGui g) {
			return cancellingClick(index, type, action, g);
		}
		return false;
	}
	
	boolean cancellingClick(int index, ClickType type, SlotActionType action, PagedGui gui);
}