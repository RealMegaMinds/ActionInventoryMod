package megaminds.actioninventory.actions;

import java.util.Arrays;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@PolyName("Group")
public sealed class GroupAction extends BasicAction permits RequirementAction, ConsumeAction {
	private static final BasicAction[] EMPTY_A = new BasicAction[0];

	private BasicAction[] actions;

	public GroupAction() {}

	public GroupAction(BasicAction[] actions) {
		this.actions = actions;
	}

	public GroupAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, BasicAction[] actions) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
		this.actions = actions;
	}

	@Override
	public void validate() {
		if (actions==null) actions = EMPTY_A;
	}

	@Override
	public void accept(ActionInventoryGui gui) {
		for (var a : actions) {
			a.accept(gui);
		}
	}

	@Override
	public BasicAction copy() {
		return new GroupAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), Arrays.stream(actions).map(BasicAction::copy).toArray(BasicAction[]::new));
	}

	public BasicAction[] getActions() {
		return actions;
	}

	public void setActions(BasicAction[] actions) {
		this.actions = actions;
	}
}
