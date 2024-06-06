package megaminds.actioninventory.gui.callback;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.BetterAnvilGui;
import megaminds.actioninventory.gui.BetterGuiI;
import net.minecraft.screen.slot.SlotActionType;

public interface BetterAnvilCallback extends BetterClickCallback {
	default boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		if (gui instanceof BetterAnvilGui g) {
			return cancellingClick(index, type, action, g);
		}
		return false;
	}
	
	boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterAnvilGui gui);
}