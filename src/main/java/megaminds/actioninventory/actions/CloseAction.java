package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.screen.slot.SlotActionType;

@NoArgsConstructor
@Poly("CloseGui")
public final class CloseAction extends BasicAction {	
	public CloseAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, String requiredGuiName) {
		super(requiredIndex, clicktype, actionType, requiredGuiName);
	}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		gui.close();
	}

	@Override
	public void validate() {
		//Unused
	}
}