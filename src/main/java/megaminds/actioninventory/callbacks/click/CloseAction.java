package megaminds.actioninventory.callbacks.click;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.screen.slot.SlotActionType;

public class CloseAction extends BasicAction {

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		gui.close();
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		return this;
	}
}