package megaminds.actioninventory.actions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.ScreenProperty;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.JsonHelper;
import net.minecraft.screen.slot.SlotActionType;

public class SendPropertyAction extends BasicAction {
	private static final String PROPERTY = "property", VALUE = "value";
	
	private ScreenProperty property;
	private int value;

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		gui.sendProperty(property, value);
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		property = JsonHelper.getOrDefault(obj.get(PROPERTY), ScreenProperty.class, context::deserialize, ScreenProperty.FIRE_LEVEL);
		value = JsonHelper.getOrDefault(obj.get(VALUE), JsonElement::getAsInt, 0);
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		obj.add(PROPERTY, context.serialize(property));
		obj.addProperty(VALUE, value);
		return obj;
	}

	@Override
	public Action getType() {
		return Action.PROPERTY;
	}
}