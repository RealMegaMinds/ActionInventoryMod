package megaminds.actioninventory.gui;

import java.util.Objects;

import eu.pb4.sgui.api.elements.AnimatedGuiElement;
import megaminds.actioninventory.actions.BasicAction;
import net.minecraft.item.ItemStack;

public class AccessableAnimatedGuiElement extends AnimatedGuiElement {

	public AccessableAnimatedGuiElement(ItemStack[] items, int interval, boolean random, BasicAction callback) {
		super(items, interval, random, Objects.requireNonNullElse(callback, EMPTY_CALLBACK));
	}
	
	public AccessableAnimatedGuiElement(ItemStack[] items, int interval, boolean random) {
		super(items, interval, random, EMPTY_CALLBACK);
	}

	public ItemStack[] getItems() {
		return items;
	}
	
	public int getInterval() {
		return changeEvery;
	}
	
	public boolean getRandom() {
		return random;
	}
	
	@Override
	public BasicAction getGuiCallback() {
		return (BasicAction) super.getGuiCallback();
	}
}