package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public interface NamedSlotGuiInterface extends SlotGuiInterface {
	Identifier getName();
	
	/**@since 3.1*/
	ItemStack getLastClickedStack();

	default ItemStack getStack(int slot) {
		GuiElementInterface e = getSlot(slot);
		if (e!=null) {
			return e.getItemStack().copy();
		}
		Slot r = getSlotRedirect(slot);
		if (r!=null) {
			return r.getStack().copy();
		}
		return ItemStack.EMPTY;
	}
}