package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.screen.slot.SlotActionType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Poly("Group")
public sealed class GroupAction extends BasicAction permits RequirementAction, ConsumeAction {
	private static final BasicAction[] EMPTY_A = new BasicAction[0];

	private BasicAction[] actions;

	public GroupAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, String requiredGuiName, BasicAction[] actions) {
		super(requiredIndex, clicktype, actionType, requiredGuiName);
		this.actions = actions;
	}

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
