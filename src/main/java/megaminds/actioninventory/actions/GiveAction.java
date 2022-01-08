package megaminds.actioninventory.actions;

import static megaminds.actioninventory.util.JsonHelper.*;

import java.util.Arrays;
import java.util.Objects;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

/**
 * This gives an item to the player (will be dropped if the player's inventory is full)
 */
public class GiveAction extends BasicAction {
	private static final String ITEMS = "itemsToGive";
	
	private ItemStack[] stacks;
	
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ItemStack current = Objects.requireNonNullElse(gui.getStack(index), ItemStack.EMPTY);
		Arrays.stream(stacks).map(s->Objects.requireNonNullElse(s, current)).forEach(s->{
			gui.getPlayer().getInventory().offerOrDrop(s.copy());
		});
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		stacks = clazzArr(obj.get(ITEMS), ItemStack.class, context, true);
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		obj.add(ITEMS, context.serialize(stacks));
		return obj;
	}

	@Override
	public Action getType() {
		return Action.GIVE;
	}
}