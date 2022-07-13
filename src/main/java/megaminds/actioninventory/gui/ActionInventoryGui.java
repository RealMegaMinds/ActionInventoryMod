package megaminds.actioninventory.gui;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import lombok.Getter;
import megaminds.actioninventory.actions.ClickAwareAction;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Getter
public class ActionInventoryGui extends BetterGui {
	/**@since 3.1*/
	private final ClickAwareAction openAction;
	/**@since 3.1*/
	private final ClickAwareAction closeAction;
	/**@since 3.1*/
	private final ClickAwareAction anyClickAction;
	/**@since 3.1*/
	private final ClickAwareAction recipeAction;
	/**@since 3.1*/
	private ItemStack lastClicked;
	/**@since 3.1*/
	private String lastAction;

	public ActionInventoryGui(ServerPlayerEntity player, ScreenHandlerType<?> type, Identifier name, Text title, boolean includePlayerInventorySlots, boolean lockPlayerInventory, boolean chained, ClickAwareAction openAction, ClickAwareAction closeAction, ClickAwareAction anyClickAction, ClickAwareAction recipeAction) {	//NOSONAR
		super(player, type, name, title, includePlayerInventorySlots, lockPlayerInventory, chained);
		this.openAction = openAction;
		this.closeAction = closeAction;
		this.anyClickAction = anyClickAction;
		this.recipeAction = recipeAction;
		this.lastClicked = ItemStack.EMPTY;
		this.lastAction = "";
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
	@NotNull
	public ItemStack getLastClicked() {
		return lastClicked.copy();
	}

	/**
	 * Returns the name of the last action. Currently only used for ConsumableAction.
	 */
	@NotNull
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
}