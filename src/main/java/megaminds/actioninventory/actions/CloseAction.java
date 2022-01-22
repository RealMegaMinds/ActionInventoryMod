package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.gui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@NoArgsConstructor
@PolyName("CloseGui")
public final class CloseAction extends BasicAction {	
	public CloseAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Boolean requireShift, Identifier requiredRecipe, Identifier requiredGuiName) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
	}
	
	@Override
	public void onRecipe(Identifier recipe, boolean shift, NamedSlotGuiInterface gui) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		gui.close();
	}

	@Override
	public void validate() {
		//Unused
	}

	@Override
	public BasicAction copy() {
		return new CloseAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequiredGuiName());
	}
}