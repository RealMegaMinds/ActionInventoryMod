package megaminds.actioninventory.callbacks.click;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

/**
 * This gives an item to the player (will be dropped if the player's inventory is full)
 */
public class GiveAction extends BasicAction {
	private static final String ITEM = "itemStackToGive";
	
	private ItemStack itemStack;
	
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		gui.getPlayer().getInventory().offerOrDrop(itemStack.copy());
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		itemStack = obj.has(ITEM) ? context.deserialize(obj.get(ITEM), ItemStack.class) : ItemStack.EMPTY;
		return this;
	}
}