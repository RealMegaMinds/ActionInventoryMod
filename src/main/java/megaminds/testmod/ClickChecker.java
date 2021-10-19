package megaminds.testmod;

import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ClickChecker {
	/**
	 * @return
	 * True - should exit method
	 * False - continue processing
	 */
	default boolean onClick(int index, int button, SlotActionType actionType, ServerPlayerEntity player) {
		return false;
	}
}