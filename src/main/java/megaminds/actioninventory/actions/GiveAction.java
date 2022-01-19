package megaminds.actioninventory.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import eu.pb4.sgui.api.ClickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
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
	private static final Identifier[] EMPTY = new Identifier[0];

	private Identifier[] lootTables;
	private boolean giveDisplayed;

	public GiveAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Identifier requiredGuiName, Identifier[] lootTables, boolean giveDisplayed) {
		super(requiredIndex, clicktype, actionType, requiredGuiName);
		this.lootTables = lootTables;
		this.giveDisplayed = giveDisplayed;
	}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity p = gui.getPlayer();

		if (giveDisplayed) {
			ItemStack current = gui.getStack(index);
			if (current!=null) p.getInventory().offerOrDrop(current);
		}

		LootContext lootContext = new LootContext.Builder(p.getWorld())
				.parameter(LootContextParameters.THIS_ENTITY, p)
				.parameter(LootContextParameters.ORIGIN, p.getPos())
				.random(p.getRandom())
				.luck(p.getLuck())
				.build(LootContextTypes.EMPTY);
		
		Arrays.stream(lootTables)
		.map(id->p.server.getLootManager().getTable(id).generateLoot(lootContext))
		.<ItemStack>mapMulti(List::forEach)
		.filter(Objects::nonNull)
		.forEach(p.getInventory()::offerOrDrop);
		
		p.currentScreenHandler.sendContentUpdates();
	}

	@Override
	public void validate() {
		if (lootTables==null) lootTables = EMPTY;
	}

	@Override
	public BasicAction copy() {
		return new GiveAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequiredGuiName(), Arrays.copyOf(lootTables, lootTables.length), giveDisplayed);
	}
}