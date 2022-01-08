package megaminds.actioninventory.actions;

import java.util.Optional;

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
	
	protected Optional<Integer> index;
	protected Optional<ClickType> clickType;
	protected Optional<SlotActionType> slotActionType;
	
	/**
	 * Checks if all given arguments match this instance's fields. If so, calls internalClick. Null fields are ignored.
	 */
	@Override
	public void click(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		if (Helper.nullOr(index, ) && Helper.nullOrEquals(clickType, type) && Helper.nullOrEquals(slotActionType, action)) {
			this.internalClick(index, type, action, gui);
		}Helper.apply(null, null, null)
	}

	public abstract void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui);
	public abstract BasicAction fromJson(JsonObject obj, JsonDeserializationContext context);
	public abstract JsonObject toJson(JsonObject obj, JsonSerializationContext context);
	public abstract Action getType();
}