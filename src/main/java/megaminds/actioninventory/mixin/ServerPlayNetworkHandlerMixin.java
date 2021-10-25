package megaminds.actioninventory.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import megaminds.actioninventory.callbacks.SignFinishCallback;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.filter.TextStream.Message;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(value = ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	@Final
	private ServerPlayerEntity player;

	/**
	 * This happens right after the sign text is changed.
	 * {@link ServerPlayNetworkHandler#onSignUpdate(UpdateSignC2SPacket)}
	 */
	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/entity/SignBlockEntity.setTextOnRow(ILnet/minecraft/text/Text;)V", shift = Shift.AFTER), method = "net/minecraft/server/network/ServerPlayNetworkHandler.onSignUpdate(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;Ljava/util/List;)V")
	private void afterSignUpdate(UpdateSignC2SPacket packet, List<Message> signText, CallbackInfo info) {
		SignFinishCallback.EVENT.invoker().onFinish(packet, signText, player);
	}
}