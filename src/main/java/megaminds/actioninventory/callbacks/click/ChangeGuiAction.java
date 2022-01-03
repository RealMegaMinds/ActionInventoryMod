package megaminds.actioninventory.callbacks.click;

import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.util.NamedGuiLoader;
import net.minecraft.screen.slot.SlotActionType;

public class ChangeGuiAction extends BasicAction {
	private static final String NAME = "name", TYPE = "guiType";
	private static final String PLAYER = "playerInventory", ENDERCHEST = "enderChest", NAMED_GUI = "gui"; 

	private String name;
	private String type;

	@Override
	public void internalClick(int index, ClickType cType, SlotActionType action, SlotGuiInterface gui) {
		gui.close();
		if (type==null) return;
			
		if (type.equals(PLAYER)) {
			UUID uuid = name==null ? gui.getPlayer().getUuid() : UUID.fromString(name);
			NamedGuiLoader.openInventory(gui.getPlayer(), uuid);
		} else if (type.equals(ENDERCHEST)) {
			UUID uuid = name==null ? gui.getPlayer().getUuid() : UUID.fromString(name);
			NamedGuiLoader.openEnderChest(gui.getPlayer(), uuid);
		} else if (type.equals(NAMED_GUI)) {
			NamedGuiLoader.openGui(gui.getPlayer(), name);
		}
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		name = obj.has(NAME) ? obj.get(NAME).getAsString() : null;
		type = obj.has(TYPE) ? obj.get(TYPE).getAsString() : null;
		return this;
	}
}