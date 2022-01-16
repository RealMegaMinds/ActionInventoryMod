package megaminds.actioninventory.gui.elements;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.EmptyAction;
import megaminds.actioninventory.gui.NamedGui;
import megaminds.actioninventory.serialization.wrappers.Validated;
import net.minecraft.server.network.ServerPlayerEntity;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract sealed class AccessableElement implements GuiElementInterface, SlotElement, Validated permits AccessableGuiElement, AccessableAnimatedGuiElement {
	@Getter private int index;
	@Setter private BasicAction action;

	@Override
	public void validate() {
		if (action==null) action = EmptyAction.INSTANCE;
	}
	
	@Override
	public BasicAction getGuiCallback() {
		return action;
	}
	
	@Override
	public void apply(NamedGui gui, ServerPlayerEntity p) {
		gui.setSlot(getCheckedIndex(gui), this);
	}
}