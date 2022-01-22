package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.Getter;
import lombok.Setter;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.EmptyAction;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@Getter
@Setter
public class NamedGui extends SimpleGui implements NamedSlotGuiInterface {
	/**
	 * Just a copy of the builder's name.
	 */
	private final Identifier name;
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
	
	public NamedGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, Identifier name) {
		super(type, player, includePlayerInventorySlots);
		this.name = name;
		this.closeAction = EmptyAction.INSTANCE;
		this.openAction = EmptyAction.INSTANCE;
		this.anyClickAction = EmptyAction.INSTANCE;
		this.recipeAction = EmptyAction.INSTANCE;
	}
	
	public NamedGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, Identifier name, BasicAction openAction, BasicAction closeAction, BasicAction anyClickAction, BasicAction recipeAction) {
		super(type, player, includePlayerInventorySlots);
		this.name = name;
		this.openAction = openAction;
		this.closeAction = closeAction;
		this.anyClickAction = anyClickAction;
		this.recipeAction = recipeAction;
	}

	/**@since 3.1*/
	@Override
	public void onClose() {
		lastClicked = ItemStack.EMPTY;
		closeAction.execute(this);
	}
	
	/**@since 3.1*/
	@Override
	public void onOpen() {
		lastClicked = ItemStack.EMPTY;
		openAction.execute(this);
	}
	
	/**@since 3.1*/
	@Override
	public boolean onAnyClick(int index, ClickType type, SlotActionType action) {
		lastClicked = getStack(index).copy();
		anyClickAction.click(index, type, action, this);
		return super.onAnyClick(index, type, action);
	}
	
	/**@since 3.1*/
	@Override
	public void onCraftRequest(Identifier recipe, boolean shift) {
		lastClicked = player.getServer().getRecipeManager().get(recipe).map(Recipe::getOutput).orElse(ItemStack.EMPTY).copy();
		recipeAction.onRecipe(recipe, shift, this);
	}

	/**@since 3.1*/
	@Override
	public ItemStack getLastClickedStack() {
		return lastClicked.copy();
	}
}