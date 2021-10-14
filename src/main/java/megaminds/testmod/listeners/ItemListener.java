package megaminds.testmod.listeners;

import megaminds.testmod.inventory.ActionInventoryManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemListener {
	public static ActionResult onItemUse(PlayerEntity player, World world, Hand hand){
		if (!world.isClient) {
			ItemStack stack = player.getStackInHand(hand);
			
			if (stack!=null && !stack.isEmpty()) {
				boolean opened = DefaultInventories.getItemUseInventories().anyMatch(i -> {
					return i.shouldOpen((ServerPlayerEntity) player, stack) && ActionInventoryManager.open(i, (ServerPlayerEntity)player);
				});
				if (opened) {
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}

	public static ActionResult onItemAttack(PlayerEntity player, World world, Hand hand){
		if (!world.isClient) {
			ItemStack stack = player.getStackInHand(hand);
			
			if (stack!=null && !stack.isEmpty()) {
				boolean opened = DefaultInventories.getItemAttackInventories().anyMatch(i -> {
					return i.shouldOpen((ServerPlayerEntity) player, stack) && ActionInventoryManager.open(i, (ServerPlayerEntity)player);
				});
				if (opened) {
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}
}