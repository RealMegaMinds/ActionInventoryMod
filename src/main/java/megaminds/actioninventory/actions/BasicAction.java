package megaminds.actioninventory.actions;

import com.google.gson.annotations.JsonAdapter;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedGuiCallback;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.serialization.PolymorphicTypeAdapterFactory;
import net.minecraft.screen.slot.SlotActionType;

@JsonAdapter(PolymorphicTypeAdapterFactory.class)
public sealed abstract class BasicAction implements NamedGuiCallback permits ChangeGuiAction, CloseAction, CommandAction, ConsumeAction, GiveAction, MessageAction, RequirementAction, SendPropertyAction, SoundAction {
	private Integer requiredIndex;
	private ClickType requiredClickType;
	private SlotActionType requiredSlotActionType;
	private String requiredGuiName;
	
	public BasicAction() {
	}
	
	public BasicAction(Integer index, ClickType clickType, SlotActionType slotActionType, String guiName) {
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
	
	/**
	 * If the optional is empty or matches the given value, returns true.
	 */
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
}