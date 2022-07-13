package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.gui.ActionInventoryGui;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class EmptyAction implements Action {
	@Override public boolean cancellingClick(int index, ClickType type, SlotActionType action, ActionInventoryGui gui) {return false;}
	@Override public void accept(ActionInventoryGui gui) { /* Does nothing */ }

	@Override
	public Identifier getType() {
		return new Identifier(ActionInventoryMod.MOD_ID, "Empty");
	}
}