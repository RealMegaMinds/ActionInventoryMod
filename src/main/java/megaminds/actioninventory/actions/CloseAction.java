package megaminds.actioninventory.actions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import net.minecraft.screen.slot.SlotActionType;

public class CloseAction extends BasicAction {

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		gui.close();
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		return obj;
	}

	@Override
	public Action getType() {
		return Action.CLOSE;
	}
}