package megaminds.actioninventory.actions;

import java.util.Arrays;
import java.util.Objects;

import eu.pb4.sgui.api.ClickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

/**
 * This gives an item to the player (will be dropped if the player's inventory is full)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PolyName("Give")
public final class GiveAction extends BasicAction {
	private static final ItemStack[] EMPTY = new ItemStack[0];
	
	private ItemStack[] itemsToGive;
	
	public GiveAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Identifier requiredGuiName, ItemStack[] itemsToGive) {
		super(requiredIndex, clicktype, actionType, requiredGuiName);
		this.itemsToGive = itemsToGive;
	}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ItemStack current = Objects.requireNonNullElse(gui.getStack(index), ItemStack.EMPTY);
		Arrays.stream(itemsToGive).map(s->Objects.requireNonNullElse(s, current)).forEach(s->gui.getPlayer().getInventory().offerOrDrop(s.copy()));
	}

	@Override
	public void validate() {
		if (itemsToGive==null) itemsToGive = EMPTY;
	}
}