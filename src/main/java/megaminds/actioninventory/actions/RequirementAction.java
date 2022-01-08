package megaminds.actioninventory.actions;

import static megaminds.actioninventory.util.JsonHelper.*;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.LevelSetter;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.Helper;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.screen.slot.SlotActionType;

public class RequirementAction extends BasicAction {
	private static final String ACTIONS = "actions", SELECTOR = "entitySelector";

	private BasicAction[] actions;
	private EntitySelector selector;
	private String selectorStr;

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		if (Helper.nullOr(selector, s-> matches(s, gui.getPlayer()))) {
			for (BasicAction a : actions) {
				a.internalClick(index, type, action, gui);
			}
		}
	}
	
	private static boolean matches(EntitySelector s, Entity e) {
		try {
			return e.equals(s.getEntity(((LevelSetter)e.getCommandSource()).withHigherLevel(2)));
		} catch (CommandSyntaxException e1) {
			return false;
		}
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		this.selectorStr = "@s"+string(obj.get(SELECTOR), "").strip();
		try {
			this.selector = new EntitySelectorReader(new StringReader(selectorStr)).read();
		} catch (CommandSyntaxException e) {
			throw new JsonSyntaxException("Failed to read entity selector for an EntityOpener.", e);
		}
		actions = clazzArr(obj.get(ACTIONS), BasicAction.class, context, false);
		return this;
	}
	
	@Override
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		obj.add(ACTIONS, context.serialize(actions));
		obj.addProperty(SELECTOR, selectorStr);
		return obj;
	}

	@Override
	public Action getType() {
		return Action.REQUIRE;
	}
}