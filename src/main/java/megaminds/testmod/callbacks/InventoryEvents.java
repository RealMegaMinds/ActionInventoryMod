package megaminds.testmod.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public class InventoryEvents {
	/**
	 * On result:
	 * - SUCCESS exits processing
	 * - PASS continues processing
	 * - FAIL exits processing
	 */
	public static final Event<BeforeSlotClick> BEFORE_SLOT_CLICK = EventFactory.createArrayBacked(BeforeSlotClick.class, listeners -> (packet, player) -> {
		for (BeforeSlotClick listener : listeners) {
			TypedActionResult<ClickSlotC2SPacket> r = listener.onBeforeSlotClick(packet, player);
			packet = r.getValue();
			if (r.getResult() != ActionResult.PASS) {
				return r;
			}
		}
		return TypedActionResult.pass(packet);
	});
	
	/**
	 * On result:
	 * - SUCCESS exits processing
	 * - PASS continues processing
	 * - FAIL exits processing
	 */
	public static final Event<AfterSlotClick> AFTER_SLOT_CLICK = EventFactory.createArrayBacked(AfterSlotClick.class, listeners -> (packet, player) -> {
		for (AfterSlotClick listener : listeners) {
			ActionResult r = listener.onAfterSlotClick(packet, player);
			if (r != ActionResult.PASS) {
				return r;
			}
		}
		return ActionResult.PASS;
	});
	
	public interface BeforeSlotClick {
		TypedActionResult<ClickSlotC2SPacket> onBeforeSlotClick(ClickSlotC2SPacket packet, ServerPlayerEntity player);
	}
	public interface AfterSlotClick {
		ActionResult onAfterSlotClick(ClickSlotC2SPacket packet, ServerPlayerEntity player);
	}
}