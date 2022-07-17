package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.EmptyAction;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ActionInventoryGui extends BetterGui {
	/**@since 3.1*/
	private final BasicAction openAction;
	/**@since 3.1*/
	private final BasicAction closeAction;
	/**@since 3.1*/
	private final BasicAction anyClickAction;
	/**@since 3.1*/
	private final BasicAction recipeAction;
	/**@since 3.1*/
	private ItemStack lastClicked;
	/**@since 3.1*/
	private String lastAction;
	
	public ActionInventoryGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, Identifier name) {
		this(type, player, includePlayerInventorySlots, name, EmptyAction.INSTANCE, EmptyAction.INSTANCE, EmptyAction.INSTANCE, EmptyAction.INSTANCE);
	}
	
	public ActionInventoryGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, Identifier name, BasicAction openAction, BasicAction closeAction, BasicAction anyClickAction, BasicAction recipeAction) {
		super(type, player, includePlayerInventorySlots);
		this.setId(name);
		this.openAction = openAction;
		this.closeAction = closeAction;
		this.anyClickAction = anyClickAction;
		this.recipeAction = recipeAction;
	}

	/**@since 3.1*/
	@Override
	public void onClose() {
		lastClicked = ItemStack.EMPTY;
		lastAction = "onClose";
		closeAction.accept(this);
	}
	
	/**@since 3.1*/
	@Override
	public void onOpen() {
		lastClicked = ItemStack.EMPTY;
		lastAction = "onOpen";
		openAction.accept(this);
	}
	
	/**@since 3.1*/
	@Override
	public boolean onAnyClick(int index, ClickType type, SlotActionType action) {
		lastClicked = getStack(index).copy();
		lastAction = "onAnyClick";
		anyClickAction.click(index, type, action, this);
		lastAction = "click"+index;
		return super.onAnyClick(index, type, action);
	}
	
	/**@since 3.1*/
	@Override
	public void onCraftRequest(Identifier recipe, boolean shift) {
		lastClicked = player.getServer().getRecipeManager().get(recipe).map(Recipe::getOutput).orElse(ItemStack.EMPTY).copy();
		lastAction = "onCraft";
		recipeAction.onRecipe(recipe, shift, this);
	}

	/**
	 * Returns the last clicked stack. Currently only used for GiveAction.
	 */
	public ItemStack getLastClicked() {
		return lastClicked.copy();
	}

	/**
	 * Returns the name of the last action. Currently only used for ConsumableAction.
	 */
	public String getLastAction() {
		return lastAction;
	}
	
	/**
	 * Returns the stack at the current slot.
	 */
	private ItemStack getStack(int slot) {
		GuiElementInterface e = getSlot(slot);
		if (e!=null) return e.getItemStack().copy();

		Slot r = getSlotRedirect(slot);
		if (r!=null) return r.getStack().copy();

		return ItemStack.EMPTY;
	}

	public BasicAction getOpenAction() {
		return openAction;
	}

	public BasicAction getCloseAction() {
		return closeAction;
	}

	public BasicAction getAnyClickAction() {
		return anyClickAction;
	}

	public BasicAction getRecipeAction() {
		return recipeAction;
	}
}