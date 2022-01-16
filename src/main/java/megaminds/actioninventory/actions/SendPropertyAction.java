package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.ScreenProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.screen.slot.SlotActionType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Poly("SendProperty")
public final class SendPropertyAction extends BasicAction {
	private ScreenProperty property;
	private int value;
	
	public SendPropertyAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, String requiredGuiName, ScreenProperty property, int value) {
		super(requiredIndex, clicktype, actionType, requiredGuiName);
		this.property = property;
		this.value = value;
	}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		gui.sendProperty(property, value);
	}

	@Override
	public void validate() {
		if (property==null) property = ScreenProperty.FIRE_LEVEL;
		Validated.validate(value>=0, "SendProperty action requires value to be 0 or above, but it is: "+value);
	}
}