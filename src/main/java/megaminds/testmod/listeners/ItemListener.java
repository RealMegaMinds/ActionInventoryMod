package megaminds.testmod.listeners;

import megaminds.testmod.inventory.ActionInventoryManager;
import megaminds.testmod.inventory.OpenRequirement.ClickType;
import megaminds.testmod.inventory.OpenRequirement.OpenType;
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
				if (ActionInventoryManager.notify((ServerPlayerEntity) player, OpenType.ITEM, ClickType.USE, stack.copy())) {
					return TypedActionResult.success(ItemStack.EMPTY);
				}
			}
		}
		return TypedActionResult.pass(ItemStack.EMPTY);
	}
}