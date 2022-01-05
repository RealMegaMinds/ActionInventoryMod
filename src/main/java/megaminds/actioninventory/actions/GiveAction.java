package megaminds.actioninventory.actions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.JsonHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

/**
 * This gives an item to the player (will be dropped if the player's inventory is full)
 */
public class GiveAction extends BasicAction {
	private static final String ITEM = "itemStackToGive";
	
	private ItemStack itemStack;	//TODO: change to multiple stacks
									//TODO: allow null in serialization to indicate the stack in the gui
	
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		gui.getPlayer().getInventory().offerOrDrop(itemStack.copy());
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		itemStack = JsonHelper.getOrDefault(obj.get(ITEM), ItemStack.class, context::deserialize, ItemStack.EMPTY);
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		obj.add(ITEM, context.serialize(itemStack));
		return obj;
	}

	@Override
	public Action getType() {
		return Action.GIVE;
	}
}