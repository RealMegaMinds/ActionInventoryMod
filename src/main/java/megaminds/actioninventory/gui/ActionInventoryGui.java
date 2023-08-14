package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.actions.BasicAction;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public interface ActionInventoryGui extends BetterGuiI {
	public void setLastClicked(ItemStack stack);
	public void setLastAction(String action);

	/**
	 * Returns the last clicked stack. Currently only used for GiveAction.
	 */
	public ItemStack getLastClicked();

	/**
	 * Returns the name of the last action. Currently only used for ConsumableAction.
	 */
	public String getLastAction();

	/**@since 3.1*/
	@Override
	default void onClose() {
		setLastClicked(ItemStack.EMPTY);
		setLastAction("onClose");
		getCloseAction().accept(this);
	}

	/**@since 3.1*/
	@Override
	default void onOpen() {
		setLastClicked(ItemStack.EMPTY);
		setLastAction("onOpen");
		getOpenAction().accept(this);
	}

	/**@since 3.1*/
	@Override
	default boolean onAnyClick(int index, ClickType type, SlotActionType action) {
		setLastClicked(getStack(index));
		setLastAction("onAnyClick");
		getAnyClickAction().click(index, type, action, this);
		setLastAction("click"+index);
		return BetterGuiI.super.onAnyClick(index, type, action);
	}

	/**@since 3.1*/
	default void onCraftRequest(Identifier recipe, boolean shift) {
		var server = getPlayer().getServer();
		setLastClicked(server.getRecipeManager().get(recipe).map(r -> r.getOutput(server.getRegistryManager())).orElse(ItemStack.EMPTY));
		setLastAction("onCraft");
		getRecipeAction().onRecipe(recipe, shift, this);
	}

	/**
	 * Returns the stack at the current slot.
	 */
	private ItemStack getStack(int slot) {
		var e = getSlot(slot);
		if (e!=null) return e.getItemStack();

		var r = getSlotRedirect(slot);
		if (r!=null) return r.getStack();

		return ItemStack.EMPTY;
	}

	public BasicAction getOpenAction();
	public BasicAction getCloseAction();
	public BasicAction getAnyClickAction();
	public BasicAction getRecipeAction();
}
