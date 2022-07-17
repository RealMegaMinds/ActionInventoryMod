package megaminds.actioninventory.gui.elements;

import org.jetbrains.annotations.NotNull;

import megaminds.actioninventory.actions.BasicAction;
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

	@NotNull
	@Override
	public ItemStack getItemStack() {
		return this.item.copy();
	}

	@Override
	public void validate() {
		super.validate();
		if (item==null) item = ItemStack.EMPTY;
	}

	@Override
	public SlotElement copy() {
		AccessableGuiElement copy = new AccessableGuiElement();
		copy.item = item.copy();
		copy.setAction(getGuiCallback().copy());
		return copy;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}
}