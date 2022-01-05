package megaminds.actioninventory.actions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedGuiCallback;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.Helper;
import net.minecraft.screen.slot.SlotActionType;

public abstract class BasicAction implements NamedGuiCallback {
	public static final int UNSET_INDEX = -1;
	
	protected int index;
	protected ClickType clickType;
	protected SlotActionType slotActionType;
	
	/**
	 * Checks if all given arguments match this instance's fields. If so, calls internalClick. Null fields are ignored.
	 */
	@Override
	public void click(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		if (this.index==UNSET_INDEX||this.index==index && Helper.nullOrEquals(clickType, type) && Helper.nullOrEquals(slotActionType, action)) {
			this.internalClick(index, type, action, gui);
		}
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ClickType getClickType() {
		return clickType;
	}

	public void setClickType(ClickType clickType) {
		this.clickType = clickType;
	}

	public SlotActionType getSlotActionType() {
		return slotActionType;
	}

	public void setSlotActionType(SlotActionType slotActionType) {
		this.slotActionType = slotActionType;
	}

	public abstract void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui);
	public abstract BasicAction fromJson(JsonObject obj, JsonDeserializationContext context);
	public abstract JsonObject toJson(JsonObject obj, JsonSerializationContext context);
	public abstract Action getType();
}