package megaminds.actioninventory.actions;

import java.util.Arrays;

import eu.pb4.sgui.api.ClickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PolyName("Group")
public sealed class GroupAction extends ClickAwareAction permits RequirementAction, ConsumeAction {
	private static final ClickAwareAction[] EMPTY_A = new ClickAwareAction[0];

	private ClickAwareAction[] actions;

	public GroupAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, ClickAwareAction[] actions) {
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
	public ClickAwareAction copy() {
		return new GroupAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), Arrays.stream(actions).map(ClickAwareAction::copy).toArray(ClickAwareAction[]::new));
	}
}
