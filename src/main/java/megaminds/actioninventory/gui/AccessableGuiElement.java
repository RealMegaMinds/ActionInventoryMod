package megaminds.actioninventory.gui;

import java.util.Objects;

import eu.pb4.sgui.api.elements.GuiElement;
import megaminds.actioninventory.actions.BasicAction;
import net.minecraft.item.ItemStack;

public class AccessableGuiElement extends GuiElement {

	public AccessableGuiElement(ItemStack item, BasicAction callback) {
		super(item, Objects.requireNonNullElse(callback, EMPTY_CALLBACK));
	}
	
	public AccessableGuiElement(ItemStack item) {
		super(item, EMPTY_CALLBACK);
	}
	
	@Override
	public BasicAction getGuiCallback() {
		return (BasicAction) super.getGuiCallback();
	}
}