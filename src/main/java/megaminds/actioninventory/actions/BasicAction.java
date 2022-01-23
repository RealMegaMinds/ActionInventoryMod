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
	private Integer requiredIndex;
	private ClickType requiredClickType;
	private SlotActionType requiredSlotActionType;
	/**@since 3.1*/
	private Boolean requireShift;
	/**@since 3.1*/
	private Identifier requiredRecipe;
	private Identifier requiredGuiName;

	/**
	 * Fields have been checked before calling this method.
	 */
	/**@since 3.1*/
	public abstract void execute(@NotNull NamedSlotGuiInterface gui);
	public abstract BasicAction copy();
	
	/**
	 * Checks if all given arguments match this instance's fields. If so, calls internalClick. Null fields are ignored.
	 */
	@Override
	public void click(int indexA, ClickType typeA, SlotActionType actionA, NamedSlotGuiInterface guiA) {
		if (check(requiredIndex, indexA) && check(requiredClickType, typeA) && check(requiredSlotActionType, actionA) && check(requiredGuiName, guiA.getName())) {
			execute(guiA);
		}
	}
	
	/**@since 3.1*/
	public void onRecipe(Identifier recipe, boolean shift, NamedSlotGuiInterface gui) {
		if (check(requiredRecipe, recipe) && check(requireShift, shift) && check(requiredGuiName, gui.getName())) {
			execute(gui);
		}
	}

	private static <E> boolean check(E o, E e) {
		return o==null || o==e;
	}
}