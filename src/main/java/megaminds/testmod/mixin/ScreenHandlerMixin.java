package megaminds.testmod.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import megaminds.testmod.ClickChecker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
	@Shadow
	@Final
	public static int EMPTY_SPACE_SLOT_INDEX;

	@Shadow
	@Final
	public DefaultedList<Slot> slots;
	
	@Shadow
	protected abstract int nextRevision();
	
	@Inject(at = @At(value = "HEAD"), method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V", cancellable = true)
	private void onClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
		if (player.world.isClient || slotIndex == EMPTY_SPACE_SLOT_INDEX) return;
		Slot s = slots.get(slotIndex);
		if (s==null || s.inventory==null) return;
		
		if (((ClickChecker)(Object) s.inventory).onClick(s.getIndex(), button, actionType, (ServerPlayerEntity)player)) {
			nextRevision();
			info.cancel();
		}
	}
}
