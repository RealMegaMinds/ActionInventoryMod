package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.Getter;
import lombok.Setter;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.EmptyAction;
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
	private BasicAction openAction;
	/**@since 3.1*/
	private BasicAction closeAction;
	/**@since 3.1*/
	private BasicAction anyClickAction;
	/**@since 3.1*/
	private BasicAction recipeAction;
	
	public NamedGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, Identifier name) {
		super(type, player, includePlayerInventorySlots);
		this.name = name;
		this.closeAction = EmptyAction.INSTANCE;
		this.openAction = EmptyAction.INSTANCE;
		this.anyClickAction = EmptyAction.INSTANCE;
	}
	
	@Override
	public void onClose() {
		closeAction.execute(this);
	}
	
	@Override
	public void onOpen() {
		openAction.execute(this);
	}
	
	@Override
	public boolean onAnyClick(int index, ClickType type, SlotActionType action) {
		anyClickAction.click(index, type, action, this);
		return super.onAnyClick(index, type, action);
	}
	
	@Override
	public void onCraftRequest(Identifier recipe, boolean shift) {
		recipeAction.onRecipe(recipe, shift, this);
	}
}