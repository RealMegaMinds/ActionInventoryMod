package megaminds.actioninventory.actions;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.gui.ActionInventoryGui;
import net.minecraft.advancement.Advancement;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class CloseAction extends BasicAction {	
	public CloseAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Boolean requireShift, Identifier requiredRecipe, Identifier requiredGuiName) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
	}

	@Override
	public void accept(@NotNull ActionInventoryGui gui) {
		gui.close();
	}

	@Override
	public Identifier getType() {
		recipe
		return new Identifier(ActionInventoryMod.MOD_ID, "Close");
	}
	
	public static CloseAction fromJson(JsonObject obj) {
		var index = obj.get("requiredIndex");
		var click = obj.get("requiredClickType");
		var action = obj.get("requiredSlotActionType");
		var shift = obj.get("requireShift");
		var recipe = obj.get("requiredRecipe");
		var gui = obj.get("requiredGuiName");
		return new CloseAction(null, null, null, null, null, null);
	}
}