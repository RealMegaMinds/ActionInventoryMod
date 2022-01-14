package megaminds.actioninventory.gui;

import com.google.gson.annotations.JsonAdapter;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.misc.Validated;
import megaminds.actioninventory.serialization.PolymorphicTypeAdapterFactory;

@JsonAdapter(PolymorphicTypeAdapterFactory.class)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public sealed abstract class AccessableElement implements GuiElementInterface, Validated permits AccessableGuiElement, AccessableAnimatedGuiElement {
	private BasicAction action;

	@Override
	public void validate() {
		if (action==null) action = BasicAction.EMPTY;
	}
	
	@Override
	public BasicAction getGuiCallback() {
		return action;
	}
}