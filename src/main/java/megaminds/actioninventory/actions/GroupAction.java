package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.TypeName;
import net.minecraft.screen.slot.SlotActionType;

@TypeName("Group")
public sealed class GroupAction extends BasicAction permits RequirementAction, ConsumeAction {
	private static final BasicAction[] EMPTY_A = new BasicAction[0];

	private BasicAction[] actions;

	@Override
	public void validate() {
		if (actions==null) actions = EMPTY_A;
	}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		for (BasicAction a : actions) {
			a.internalClick(index, type, action, gui);
		}
	}
}
