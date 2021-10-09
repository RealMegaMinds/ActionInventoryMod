package megaminds.testmod.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import megaminds.testmod.callbacks.InventoryEvents;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
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

	@Inject(at = {@At(value = "TAIL")}, method = "onClickSlot(Lnet/minecraft/network/packet/c2s/play/ClickSlotC2SPacket;)V")
	void afterSlotClick(ClickSlotC2SPacket packet, CallbackInfo info) {
		InventoryEvents.AFTER_SLOT_CLICK.invoker().onAfterSlotClick(packet, player);
	}

	//TODO maybe add these
//	void onCraftRequest(CraftRequestC2SPacket packet);

//	void onCloseHandledScreen(CloseHandledScreenC2SPacket packet);

//	void onPlayerAction(PlayerActionC2SPacket packet);

//	void onClientCommand(ClientCommandC2SPacket packet);

//	void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet);

//	void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet);

//	void onSignUpdate(UpdateSignC2SPacket packet);

//	void onPickFromInventory(PickFromInventoryC2SPacket packet);
}