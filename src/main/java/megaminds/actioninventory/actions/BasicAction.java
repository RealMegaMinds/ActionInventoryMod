package megaminds.actioninventory.actions;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.gui.callback.ActionInventoryCallback;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Poly;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@Poly
public abstract sealed class BasicAction implements ActionInventoryCallback, Validated, Consumer<ActionInventoryGui> permits EmptyAction, OpenGui, CloseAction, CommandAction, GiveAction, MessageAction, SendPropertyAction, SoundAction, GroupAction, AnvilStoreAction {
	private Integer requiredIndex;
	private ClickType requiredClickType;
	private SlotActionType requiredSlotActionType;
	/**@since 3.1*/
	private TriState requireShift = TriState.DEFAULT;
	/**@since 3.1*/
	private Identifier requiredRecipe;
	private Identifier requiredGuiName;
	
	protected BasicAction() {}
	
	protected BasicAction(Integer requiredIndex, ClickType requiredClickType, SlotActionType requiredSlotActionType, TriState requireShift, Identifier requiredRecipe, Identifier requiredGuiName) {
		this.requiredIndex = requiredIndex;
		this.requiredClickType = requiredClickType;
		this.requiredSlotActionType = requiredSlotActionType;
		this.requireShift = requireShift;
		this.requiredRecipe = requiredRecipe;
		this.requiredGuiName = requiredGuiName;
	}
	
	/**
	 * Fields have been checked before calling this method.
	 */
	/**@since 3.1*/
	@Override
	public abstract void accept(@NotNull ActionInventoryGui gui);
	public abstract BasicAction copy();
	
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

	public Integer getRequiredIndex() {
		return requiredIndex;
	}

	public void setRequiredIndex(Integer requiredIndex) {
		this.requiredIndex = requiredIndex;
	}

	public ClickType getRequiredClickType() {
		return requiredClickType;
	}

	public void setRequiredClickType(ClickType requiredClickType) {
		this.requiredClickType = requiredClickType;
	}

	public SlotActionType getRequiredSlotActionType() {
		return requiredSlotActionType;
	}

	public void setRequiredSlotActionType(SlotActionType requiredSlotActionType) {
		this.requiredSlotActionType = requiredSlotActionType;
	}

	public TriState getRequireShift() {
		return requireShift;
	}

	public void setRequireShift(TriState requireShift) {
		this.requireShift = requireShift;
	}

	public Identifier getRequiredRecipe() {
		return requiredRecipe;
	}

	public void setRequiredRecipe(Identifier requiredRecipe) {
		this.requiredRecipe = requiredRecipe;
	}

	public Identifier getRequiredGuiName() {
		return requiredGuiName;
	}

	public void setRequiredGuiName(Identifier requiredGuiName) {
		this.requiredGuiName = requiredGuiName;
	}
}