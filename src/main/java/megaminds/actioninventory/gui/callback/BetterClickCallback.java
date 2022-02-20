package megaminds.actioninventory.gui.callback;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.gui.BetterGuiI;
import net.minecraft.screen.slot.SlotActionType;

public interface BetterClickCallback extends CancellableCallback {
	@Override
	default boolean cancellingClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		if (gui instanceof BetterGuiI g) {
			return cancellingClick(index, type, action, g);
		}
		return false;
	}
	
	boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui);
}