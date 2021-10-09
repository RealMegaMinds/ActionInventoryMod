package megaminds.testmod;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;

/**
 * Callback for clicking an inventory slot.
 * Called before other processing.
 * 
 * Upon return:<br>
 * - SUCCESS: Cancels further processing and continues normal processing. <br>
 * - PASS: Falls back to further processing. <br>
 * - FAIL: Cancels further processing.
 */
public interface InventoryClickCallback {	
	Event<InventoryClickCallback> EVENT = EventFactory.createArrayBacked(InventoryClickCallback.class, listeners -> (slot, button, slotActionType, player) -> {
		for (InventoryClickCallback listener : listeners) {
			ActionResult result = listener.doClick(slot, button, slotActionType, player);
			
			if (result != ActionResult.PASS) {
				return result;
			}
		}
		return ActionResult.PASS;
	});
	
	/**
	 * Slot can be null if player clicks outside of inventory.
	 */
	ActionResult doClick(@Nullable Slot slot, int button, SlotActionType sloActionType, PlayerEntity player);
}