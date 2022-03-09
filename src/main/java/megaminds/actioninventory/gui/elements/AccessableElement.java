package megaminds.actioninventory.gui.elements;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.EmptyAction;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.serialization.wrappers.Validated;
import net.minecraft.server.network.ServerPlayerEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract sealed class AccessableElement extends SlotElement implements GuiElementInterface, Validated permits AccessableGuiElement, AccessableAnimatedGuiElement {
	@Setter private BasicAction action;

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
}