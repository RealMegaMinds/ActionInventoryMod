package megaminds.actioninventory.gui.elements;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.sgui.api.gui.GuiInterface;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.item.ItemStack;

/**
 * Adapted from {@link eu.pb4.sgui.api.elements.GuiElement}
 */
@PolyName("Normal")
public final class AccessableGuiElement extends AccessableElement {
	private ItemStack item;

	public AccessableGuiElement() {}

	public AccessableGuiElement(int index, BasicAction action, ItemStack item) {
		super(index, action);
		this.item = item;
	}

	@Override
	public ItemStack getItemStackForDisplay(GuiInterface gui) {
		lastDisplayed = Helper.parseItemStack(item.copy(), PlaceholderContext.of(gui.getPlayer()));
		return lastDisplayed;
	}

	@Override
	public void validate() {
		super.validate();
		if (item==null) item = ItemStack.EMPTY;
	}

	@Override
	public SlotElement copy() {
		return new AccessableGuiElement(getIndex(), getGuiCallback().copy(), item.copy());
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}
}