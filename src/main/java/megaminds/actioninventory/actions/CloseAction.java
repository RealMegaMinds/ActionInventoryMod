package megaminds.actioninventory.actions;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@PolyName("CloseGui")
public final class CloseAction extends BasicAction {
	public CloseAction() {}
	
	public CloseAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe, Identifier requiredGuiName) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
	}
	
	@Override
	public void validate() {
		//Unused
	}

	@Override
	public BasicAction copy() {
		return new CloseAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName());
	}

	@Override
	public void accept(@NotNull ActionInventoryGui gui) {
		gui.close();
	}
}