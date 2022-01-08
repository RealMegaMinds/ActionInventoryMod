package megaminds.actioninventory.actions;

import static megaminds.actioninventory.util.JsonHelper.*;

import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.NamedGuiLoader;
import net.minecraft.screen.slot.SlotActionType;

public class ChangeGuiAction extends BasicAction {
	private static final String NAME = "name", TYPE = "guiType";
	private enum GuiType {PLAYER, ENDER_CHEST, NAMED_GUI}

	private String name;
	private GuiType type;

	@Override
	public void internalClick(int index, ClickType cType, SlotActionType action, NamedSlotGuiInterface gui) {
		gui.close();
		switch (type) {
		case ENDER_CHEST -> {
			UUID uuid = Helper.apply(name, UUID::fromString, gui.getPlayer()::getUuid);
			NamedGuiLoader.openEnderChest(gui.getPlayer(), uuid);
		}
		case NAMED_GUI -> {
			NamedGuiLoader.openGui(gui.getPlayer(), name);
		}
		case PLAYER -> {
			UUID uuid = Helper.apply(name, UUID::fromString, gui.getPlayer()::getUuid);
			NamedGuiLoader.openInventory(gui.getPlayer(), uuid);
		}
		}
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		name = string(obj.get(NAME));
		type = clazz(obj.get(TYPE), GuiType.class, context, ()->GuiType.NAMED_GUI);
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		obj.addProperty(NAME, name);
		obj.add(TYPE, context.serialize(type));
		return obj;
	}

	@Override
	public Action getType() {
		return Action.OPEN_GUI;
	}
}