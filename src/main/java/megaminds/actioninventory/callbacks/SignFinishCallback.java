package megaminds.actioninventory.callbacks;

import java.util.List;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.filter.TextStream.Message;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This is happens right after a sign's text is changed.
 */
public interface SignFinishCallback {
	public static final Event<SignFinishCallback> EVENT = EventFactory.createArrayBacked(SignFinishCallback.class, listeners -> (packet, signText, player)->{
		for (SignFinishCallback listener : listeners) {
			listener.onFinish(packet, signText, player);
		}
	});
	
	void onFinish(UpdateSignC2SPacket packet, List<Message> signText, ServerPlayerEntity player);
}