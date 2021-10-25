package megaminds.testmod.listeners;

import megaminds.testmod.inventory.ActionManager;
import megaminds.testmod.inventory.openers.Opener.ClickType;
import megaminds.testmod.inventory.openers.Opener.What;
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
				if (ActionManager.notify((ServerPlayerEntity) player, click, What.Entity, entity)) {
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}
}