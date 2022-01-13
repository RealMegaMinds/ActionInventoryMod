package megaminds.actioninventory.actions;

import java.util.Arrays;
import java.util.Objects;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.TypeName;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

/**
 * This gives an item to the player (will be dropped if the player's inventory is full)
 */
@TypeName("Give")
public final class GiveAction extends BasicAction {
	private ItemStack[] itemsToGive;
	
	private GiveAction() {}
	
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ItemStack current = Objects.requireNonNullElse(gui.getStack(index), ItemStack.EMPTY);
		if (itemsToGive==null) itemsToGive = new ItemStack[0];
		Arrays.stream(itemsToGive).map(s->Objects.requireNonNullElse(s, current)).forEach(s->gui.getPlayer().getInventory().offerOrDrop(s.copy()));
	}
}