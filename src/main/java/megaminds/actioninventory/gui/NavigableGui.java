package megaminds.actioninventory.gui;

import java.util.function.Consumer;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import megaminds.actioninventory.gui.callback.BetterClickCallback;
import megaminds.actioninventory.gui.elements.Element;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public interface NavigableGui extends BetterGuiI {
	default void goToFirst() {
		goTo(0);
	}
	default void goToLast() {
		goTo(getCount());
	}
	default void goToNext() {
		goTo(getCurrent()+1);
	}
	default void goToPrevious() {
		goTo(getCurrent()-1);
	}
	void goTo(int i);
	int getCurrent();
	int getCount();
	void addCallback(Consumer<NavigableGui> onNavigate);
	void removeCallback(Consumer<NavigableGui> onNavigate);
	int getAbsoluteSize();
	void setControlSlot(int index, GuiElementInterface element);
	void setControlRedirect(int index, Slot redirect);
	void setControlElement(int index, Element element);
	boolean isRestrictedControlIndex(int i);

	public static interface NavigableCallback extends BetterClickCallback {
		@Override
		default boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
			if (gui instanceof NavigableGui g) {
				return cancellingClick(index, type, action, g);
			}
			return false;
		}

		boolean cancellingClick(int index, ClickType type, SlotActionType action, NavigableGui gui);
	}
}