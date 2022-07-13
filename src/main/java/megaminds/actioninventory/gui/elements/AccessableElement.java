package megaminds.actioninventory.gui.elements;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.actions.ClickAwareAction;
import megaminds.actioninventory.actions.EmptyAction;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.serialization.wrappers.Validated;
import net.minecraft.server.network.ServerPlayerEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract sealed class AccessableElement extends SlotElement implements GuiElementInterface, Validated permits AccessableGuiElement, AccessableAnimatedGuiElement {
	private ClickAwareAction action;

	protected AccessableElement(int index, ClickAwareAction action) {
		super(index);
		this.action = action;
	}

	@Override
	public void validate(@NotNull Consumer<String> errorReporter) {
		if (action==null) action = EmptyAction.INSTANCE;
	}

	@Override
	public ClickAwareAction getGuiCallback() {
		return action;
	}

	@Override
	public void apply(ActionInventoryGui gui, ServerPlayerEntity p) {
		gui.setSlot(getCheckedIndex(gui), this);
	}
}