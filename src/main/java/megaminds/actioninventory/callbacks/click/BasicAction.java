package megaminds.actioninventory.callbacks.click;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.util.Helper;
import net.minecraft.screen.slot.SlotActionType;

public abstract class BasicAction implements ClickCallback {
	public static final int UNSET_INDEX = -1;
	
	protected int index;
	protected ClickType clickType;
	protected SlotActionType slotActionType;
	protected SlotGuiInterface gui;

	/**
	 * Checks if all given arguments match this instance's fields. Null fields are not ignored.
	 */
	@Override
	public void click(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		if (this.index==UNSET_INDEX||this.index==index && Helper.nullOrEquals(clickType, type) && Helper.nullOrEquals(slotActionType, action) && Helper.nullOrEquals(this.gui, gui)) {
			this.internalClick();
		}
	}
	
	public abstract void internalClick();
}