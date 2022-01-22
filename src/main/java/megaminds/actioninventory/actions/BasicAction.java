package megaminds.actioninventory.actions;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.NamedGuiCallback;
import megaminds.actioninventory.gui.NamedSlotGuiInterface;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Poly
public abstract sealed class BasicAction implements NamedGuiCallback, Validated permits EmptyAction, OpenGui, CloseAction, CommandAction, GiveAction, MessageAction, SendPropertyAction, SoundAction, GroupAction {
	private enum GuiAction {SLOT, RECIPE, OTHER}
	
	private Integer requiredIndex;
	private ClickType requiredClickType;
	private SlotActionType requiredSlotActionType;
	/**@since 3.1*/
	private Boolean requireShift;
	/**@since 3.1*/
	private Identifier requiredRecipe;
	private Identifier requiredGuiName;

	/**
	 * index, type, action, and guiName are used for SLOT.
	 * recipe, and shift or used for RECIPE.
	 * Parameters are checked before calling this method.
	 */
	public abstract void execute(int index, ClickType type, SlotActionType action, Identifier recipe, boolean shift, @NotNull GuiAction guiAction, @NotNull NamedSlotGuiInterface gui);
	public abstract BasicAction copy();
	
	/**
	 * Checks if all given arguments match this instance's fields. If so, calls internalClick. Null fields are ignored.
	 */
	@Override
	public void click(int indexA, ClickType typeA, SlotActionType actionA, NamedSlotGuiInterface guiA) {
		if (check(requiredIndex, indexA) && check(requiredClickType, typeA) && check(requiredSlotActionType, actionA) && check(requiredGuiName, guiA.getName())) {
			execute(indexA, typeA, actionA, null, false, GuiAction.SLOT, guiA);
		}
	}
	
	/**@since 3.1*/
	public void onRecipe(Identifier recipe, boolean shift, NamedSlotGuiInterface gui) {
		if (check(requiredRecipe, recipe) && check(requireShift, shift) && check(requiredGuiName, gui.getName())) {
			execute(-1, null, null, recipe, shift, GuiAction.RECIPE, gui);
		}
	}
	
	/**@since 3.1*/
	public void execute(NamedSlotGuiInterface gui) {
		execute(0, null, null, null, true, GuiAction.OTHER, gui);
	}
	
	private static <E> boolean check(E o, E e) {
		return o==null || o==e;
	}
}