package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.ScreenProperty;
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
@PolyName("SendProperty")
public final class SendPropertyAction extends ClickAwareAction {
	private ScreenProperty property;
	private int value;

	public SendPropertyAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, ScreenProperty property, int value) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
		this.property = property;
		this.value = value;
	}

	@Override
	public void accept(ActionInventoryGui gui) {
		gui.sendProperty(property, value);
	}

	@Override
	public void validate() {
		if (property==null) property = ScreenProperty.FIRE_LEVEL;
	}

	@Override
	public ClickAwareAction copy() {
		return new SendPropertyAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), property, value);
	}
}