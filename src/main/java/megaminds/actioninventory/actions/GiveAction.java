package megaminds.actioninventory.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

/**
 * This gives an item to the player (will be dropped if the player's inventory is full)
 */
@PolyName("Give")
public final class GiveAction extends BasicAction {
	private static final Identifier[] EMPTY = new Identifier[0];

	private Identifier[] lootTables;
	private TriState giveClicked = TriState.DEFAULT;

	public GiveAction() {}

	public GiveAction(Identifier[] lootTables, TriState giveClicked) {
		this.lootTables = lootTables;
		this.giveClicked = giveClicked;
	}

	public GiveAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, Identifier[] lootTables, TriState giveClicked) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
		this.lootTables = lootTables;
		this.giveClicked = giveClicked;
	}

	@Override
	public void accept(ActionInventoryGui gui) {
		var p = gui.getPlayer();

		if (giveClicked.orElse(false)) {
			var current = gui.getLastClicked().copy();
			if (current!=null) p.getInventory().offerOrDrop(current);
		}

		var lootContext = new LootContext.Builder(p.getWorld())
				.parameter(LootContextParameters.THIS_ENTITY, p)
				.parameter(LootContextParameters.ORIGIN, p.getPos())
				.random(p.getRandom())
				.luck(p.getLuck())
				.build(LootContextTypes.ADVANCEMENT_REWARD);

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
		return new GiveAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), Arrays.copyOf(lootTables, lootTables.length), giveClicked);
	}

	public Identifier[] getLootTables() {
		return lootTables;
	}

	public void setLootTables(Identifier[] lootTables) {
		this.lootTables = lootTables;
	}

	public TriState getGiveClicked() {
		return giveClicked;
	}

	public void setGiveClicked(TriState giveClicked) {
		this.giveClicked = giveClicked;
	}
}