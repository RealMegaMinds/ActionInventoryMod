package megaminds.actioninventory.actions;

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
	private Identifier requiredGuiName;

	/**
	 * index, type, action and guiName, have already been checked before this is called.
	 */
	public abstract void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui);

	/**
	 * Checks if all given arguments match this instance's fields. If so, calls internalClick. Null fields are ignored.
	 */
	@Override
	public void click(int indexA, ClickType typeA, SlotActionType actionA, NamedSlotGuiInterface guiA) {
		if (check(requiredIndex, indexA) && check(requiredClickType, typeA) && check(requiredSlotActionType, actionA) && check(requiredGuiName, guiA.getName())) {
			internalClick(indexA, typeA, actionA, guiA);
		}
	}
	
	protected static <E> boolean check(E o, E e) {
		return o==null || o==e;
	}

	public abstract BasicAction copy();
}