package megaminds.testmod.listeners;

import megaminds.testmod.inventory.ActionManager;
import megaminds.testmod.inventory.openers.Opener.ClickType;
import megaminds.testmod.inventory.openers.Opener.What;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemListener {
	public static TypedActionResult<ItemStack> onItemUse(PlayerEntity player, World world, Hand hand){
		if (!world.isClient) {
			ItemStack stack = player.getStackInHand(hand);
			if (stack!=null && !stack.isEmpty()) {
				if (ActionManager.notify((ServerPlayerEntity) player, ClickType.USE, What.ItemStack, stack.copy())) {
					return TypedActionResult.success(ItemStack.EMPTY);
				}
			}
		}
		return TypedActionResult.pass(ItemStack.EMPTY);
	}
}