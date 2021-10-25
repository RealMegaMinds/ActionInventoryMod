package megaminds.actioninventory.listeners;

import megaminds.actioninventory.inventory.ActionManager;
import megaminds.actioninventory.inventory.openers.Opener.ClickType;
import megaminds.actioninventory.inventory.openers.Opener.What;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BlockListener {
	public static ActionResult onBlockAttack(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction){
		return onBlockClick(player, world, pos, ClickType.ATTACK);
	}
	
	public static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult result){
		return onBlockClick(player, world, result.getBlockPos(), ClickType.USE);
	}
	
	private static ActionResult onBlockClick(PlayerEntity player, World world, BlockPos pos, ClickType click){
		if (!world.isClient) {
			ServerPlayerEntity p = (ServerPlayerEntity)player;
			boolean opened = false;
			
			BlockState s = world.getBlockState(pos);
			if (s!=null&&!s.isAir()&&s.getBlock()!=null) {
				opened = ActionManager.notify(p, click, What.Block, s.getBlock());
			}
			
			BlockEntity e = world.getBlockEntity(pos);
			if (!opened && e!=null) {
				opened = ActionManager.notify(p, click, What.BlockEntity, e);
			}
			
			if (opened) {
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}
}