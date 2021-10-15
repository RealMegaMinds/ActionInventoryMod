package megaminds.testmod.listeners;

import megaminds.testmod.inventory.ActionInventoryManager;
import megaminds.testmod.inventory.OpenRequirement.ClickType;
import megaminds.testmod.inventory.OpenRequirement.OpenType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class EntityListener {
	public static ActionResult onEntityUse(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
		return onEntityClick(player, world, entity, hitResult, ClickType.USE);
	}
	
	public static ActionResult onEntityAttack(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
		return onEntityClick(player, world, entity, hitResult, ClickType.ATTACK);
	}
	
	private static ActionResult onEntityClick(PlayerEntity player, World world, Entity entity, EntityHitResult hitResult, ClickType click) {
		if (!world.isClient) {
			if (entity!=null) {
				if (ActionInventoryManager.notify((ServerPlayerEntity) player, OpenType.ENTITY, click, entity)) {
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}
}