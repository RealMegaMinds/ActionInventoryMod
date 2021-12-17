package megaminds.actioninventory.callbacks.click;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.ScreenProperty;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.screen.slot.SlotActionType;

public class SendPropertyAction extends BasicAction {
	private static final String PROPERTY = "property", VALUE = "value";
	
	private ScreenProperty property;
	private int value;

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		gui.sendProperty(property, value);
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		property = obj.has(PROPERTY) ? context.deserialize(obj.get(PROPERTY), ScreenProperty.class) : ScreenProperty.FIRE_LEVEL;
		value = obj.has(VALUE) ? obj.get(VALUE).getAsInt() : 0;
		return this;
	}
}