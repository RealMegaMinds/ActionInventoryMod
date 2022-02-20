package megaminds.actioninventory.actions;

import java.util.Arrays;

import eu.pb4.sgui.api.ClickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PolyName("Group")
public sealed class GroupAction extends BasicAction permits RequirementAction, ConsumeAction {
	private static final BasicAction[] EMPTY_A = new BasicAction[0];

	private BasicAction[] actions;

	public GroupAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Boolean requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, BasicAction[] actions) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
		this.actions = actions;
	}

	@Override
	public void validate() {
		if (actions==null) actions = EMPTY_A;
	}

	@Override
	public void accept(ActionInventoryGui gui) {
		for (BasicAction a : actions) {
			a.accept(gui);
		}
	}

	@Override
	public BasicAction copy() {
		return new GroupAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), Arrays.stream(actions).map(BasicAction::copy).toArray(BasicAction[]::new));
	}
}
