package megaminds.actioninventory.actions;

import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import lombok.Getter;
import megaminds.actioninventory.gui.ActionInventoryGui;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@Getter
public abstract class ClickAwareAction extends Action {	
	private final Integer requiredIndex;
	private final ClickType requiredClickType;
	private final SlotActionType requiredSlotActionType;
	/**@since 3.1*/
	private final Boolean requireShift;
	/**@since 3.1*/
	private final Identifier requiredRecipe;
	private final Identifier requiredGuiName;

	protected ClickAwareAction(Integer requiredIndex, ClickType requiredClickType, SlotActionType requiredSlotActionType, Boolean requireShift, Identifier requiredRecipe, Identifier requiredGuiName) {
		this.requiredIndex = requiredIndex;
		this.requiredClickType = requiredClickType;
		this.requiredSlotActionType = requiredSlotActionType;
		this.requireShift = requireShift;
		this.requiredRecipe = requiredRecipe;
		this.requiredGuiName = requiredGuiName;
	}

	/**
	 * Checks if all given arguments match this instance's fields. If so, calls internalClick. Null fields are ignored.
	 */
	@Override
	public boolean cancellingClick(int indexA, ClickType typeA, SlotActionType actionA, ActionInventoryGui guiA) {
		if (check(requiredIndex, indexA) && check(requiredClickType, typeA) && check(requiredSlotActionType, actionA) && check(requiredGuiName, guiA.getId())) {
			accept(guiA);
		}
		return false;
	}

	/**@since 3.1*/
	public void onRecipe(Identifier recipe, boolean shift, ActionInventoryGui gui) {
		if (check(requiredRecipe, recipe) && check(requireShift, shift) && check(requiredGuiName, gui.getId())) {
			accept(gui);
		}
	}

	private static <E> boolean check(E o, E e) {
		return o==null || o==e;
	}
}