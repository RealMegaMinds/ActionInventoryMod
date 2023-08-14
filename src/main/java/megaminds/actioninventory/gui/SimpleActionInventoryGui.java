package megaminds.actioninventory.gui;

import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.EmptyAction;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SimpleActionInventoryGui extends BetterGui implements ActionInventoryGui {
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
	
	public SimpleActionInventoryGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, Identifier name) {
		this(type, player, includePlayerInventorySlots, name, EmptyAction.INSTANCE, EmptyAction.INSTANCE, EmptyAction.INSTANCE, EmptyAction.INSTANCE);
	}
	
	public SimpleActionInventoryGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, Identifier name, BasicAction openAction, BasicAction closeAction, BasicAction anyClickAction, BasicAction recipeAction) {
		super(type, player, includePlayerInventorySlots);
		this.setId(name);
		this.openAction = openAction;
		this.closeAction = closeAction;
		this.anyClickAction = anyClickAction;
		this.recipeAction = recipeAction;
	}
	
	@Override
	public void setLastClicked(ItemStack stack) {
		lastClicked = stack.copy();
	}
	
	@Override
	public void setLastAction(String action) {
		lastAction = action;
	}

	/**
	 * Returns the last clicked stack. Currently only used for GiveAction.
	 */
	@Override
	public ItemStack getLastClicked() {
		return lastClicked.copy();
	}

	/**
	 * Returns the name of the last action. Currently only used for ConsumableAction.
	 */
	@Override
	public String getLastAction() {
		return lastAction;
	}
	
	@Override
	public BasicAction getOpenAction() {
		return openAction;
	}

	@Override
	public BasicAction getCloseAction() {
		return closeAction;
	}

	@Override
	public BasicAction getAnyClickAction() {
		return anyClickAction;
	}

	@Override
	public BasicAction getRecipeAction() {
		return recipeAction;
	}
}