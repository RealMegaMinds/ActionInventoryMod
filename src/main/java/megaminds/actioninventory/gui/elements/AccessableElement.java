package megaminds.actioninventory.gui.elements;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.EmptyAction;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Exclude;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract sealed class AccessableElement extends SlotElement implements GuiElementInterface, Validated permits AccessableGuiElement, AccessableAnimatedGuiElement {
	private BasicAction action;
	@Exclude
	protected ItemStack lastDisplayed;

	protected AccessableElement() {}

	protected AccessableElement(int index, BasicAction action) {
		super(index);
		this.action = action;
	}

	@Override
	public void validate() {
		if (action==null) action = EmptyAction.INSTANCE;
	}

	@Override
	public BasicAction getGuiCallback() {
		return action;
	}

	@Override
	public void apply(ActionInventoryGui gui, ServerPlayerEntity p) {
		gui.setSlot(getCheckedIndex(gui), this);
	}

	public void setAction(BasicAction action) {
		this.action = action;
	}

	@Override
	public ItemStack getItemStack() {
		return lastDisplayed;
	}
}