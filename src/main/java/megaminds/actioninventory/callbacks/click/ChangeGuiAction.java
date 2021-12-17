package megaminds.actioninventory.callbacks.click;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.NamedGuisHolder;
import net.minecraft.screen.slot.SlotActionType;

public class ChangeGuiAction extends BasicAction {
	private static final String NAME = "name";

	private String name;

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		gui.close();
		if (name!=null) {
			NamedGuisHolder.openGui(gui.getPlayer(), name);
		}
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		name = obj.has(NAME) ? obj.get(NAME).getAsString() : null;
		return this;
	}
}