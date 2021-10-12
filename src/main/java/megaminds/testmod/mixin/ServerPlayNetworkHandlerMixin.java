package megaminds.testmod.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import megaminds.testmod.callbacks.InventoryEvents;
import megaminds.testmod.callbacks.SignFinishCallback;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.filter.TextStream.Message;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(value = ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	@Final
	private ServerPlayerEntity player;

	@ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/network/NetworkThreadUtils.forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V", shift = At.Shift.AFTER), method = "onClickSlot(Lnet/minecraft/network/packet/c2s/play/ClickSlotC2SPacket;)V")
	private ClickSlotC2SPacket beforeSlotClick(ClickSlotC2SPacket packet) {
		return InventoryEvents.BEFORE_SLOT_CLICK.invoker().onBeforeSlotClick(packet, player).getValue();
	}

	@Inject(at = @At(value = "TAIL"), method = "onClickSlot(Lnet/minecraft/network/packet/c2s/play/ClickSlotC2SPacket;)V")
	private void afterSlotClick(ClickSlotC2SPacket packet, CallbackInfo info) {
		InventoryEvents.AFTER_SLOT_CLICK.invoker().onAfterSlotClick(packet, player);
	}
	
	/**
	 * This happens right after the sign text is changed.
	 * {@link ServerPlayNetworkHandler#onSignUpdate(UpdateSignC2SPacket)}
	 */
	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/entity/SignBlockEntity.setTextOnRow(ILnet/minecraft/text/Text;)V", shift = Shift.AFTER), method = "net/minecraft/server/network/ServerPlayNetworkHandler.onSignUpdate(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;Ljava/util/List;)V")
	private void afterSignUpdate(UpdateSignC2SPacket packet, List<Message> signText, CallbackInfo info) {
		SignFinishCallback.EVENT.invoker().onFinish(packet, signText, player);
	}
}