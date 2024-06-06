package megaminds.actioninventory.gui.callback;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.screen.slot.SlotActionType;

public interface CancellableCallback extends ClickCallback {
	public static final CancellableCallback EMPTY = new CancellableCallback() {@Override public boolean cancellingClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {return false;}};

	@Override
	default void click(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		cancellingClick(index, type, action, gui);
	}

	/**
	 * Returns true if further callbacks should be cancelled.
	 */
	boolean cancellingClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui);

	default CancellableCallback andThen(ClickCallback callback) {
		if (callback==null || callback==EMPTY) return this;
		return (i,t,a,g)->{
			if (this.cancellingClick(i, t, a, g)) return true;
			if (callback instanceof CancellableCallback c) {
				return c.cancellingClick(i, t, a, g);
			} else {
				callback.click(i, t, a, g);
				return false;
			}
		};
	}
}