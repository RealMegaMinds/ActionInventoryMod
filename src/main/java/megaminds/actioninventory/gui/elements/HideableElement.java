package megaminds.actioninventory.gui.elements;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.GuiInterface;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;

@Getter
@Setter
public class HideableElement extends GuiElement {
	private boolean hidden;
	
	public HideableElement(ItemStack item, ClickCallback callback) {
		super(item, callback);
	}
	
	@Override
	public ItemStack getItemStackForDisplay(GuiInterface gui) {
		return hidden ? ItemStack.EMPTY : super.getItemStackForDisplay(gui);
	}
}