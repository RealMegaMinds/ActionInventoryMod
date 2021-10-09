package megaminds.testmod.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import megaminds.testmod.InventoryClickCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public class InventoryClickMixin {
	@Shadow
	@Final
	public DefaultedList<Slot> slots;
	@Shadow
	@Final
	public static int EMPTY_SPACE_SLOT_INDEX;

	@Inject(method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"), cancellable = true)
	private void onClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
		ActionResult result = InventoryClickCallback.EVENT.invoker().doClick(slotIndex!=EMPTY_SPACE_SLOT_INDEX ? slots.get(slotIndex) : null, button, actionType, player);
		if (result == ActionResult.FAIL) {
			info.cancel();
		}
	}
}