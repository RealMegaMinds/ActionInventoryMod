package megaminds.actioninventory.actions;

import com.google.gson.annotations.JsonAdapter;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedGuiCallback;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.misc.Validated;
import megaminds.actioninventory.serialization.PolymorphicTypeAdapterFactory;
import net.minecraft.screen.slot.SlotActionType;
import megaminds.actioninventory.actions.BasicAction.EmptyAction;

@JsonAdapter(PolymorphicTypeAdapterFactory.class)
public abstract sealed class BasicAction implements NamedGuiCallback, Validated permits EmptyAction, ChangeGuiAction, CloseAction, CommandAction, GiveAction, MessageAction, SendPropertyAction, SoundAction, GroupAction {
	public static final BasicAction EMPTY = new EmptyAction();
	
	private Integer requiredIndex;
	private ClickType requiredClickType;
	private SlotActionType requiredSlotActionType;
	private String requiredGuiName;
	
	protected BasicAction() {
	}
	
	protected BasicAction(Integer index, ClickType clickType, SlotActionType slotActionType, String guiName) {
		setIndex(index);
		setClickType(clickType);
		setSlotActionType(slotActionType);
		setGuiName(guiName);
	}

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

	public Integer getIndex() {
		return requiredIndex;
	}

	public void setIndex(Integer index) {
		this.requiredIndex = index;
	}

	public ClickType getClickType() {
		return requiredClickType;
	}

	public void setClickType(ClickType clickType) {
		this.requiredClickType = clickType;
	}

	public SlotActionType getSlotActionType() {
		return requiredSlotActionType;
	}

	public void setSlotActionType(SlotActionType slotActionType) {
		this.requiredSlotActionType = slotActionType;
	}

	public String getGuiName() {
		return requiredGuiName;
	}

	public void setGuiName(String guiName) {
		this.requiredGuiName = guiName;
	}
	
	static final class EmptyAction extends BasicAction {
		private EmptyAction() {}
		@Override public void validate() {}
		@Override public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {}
	}
}