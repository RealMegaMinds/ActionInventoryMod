package megaminds.testmod.listeners;

import megaminds.testmod.inventory.ActionInventoryManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
	public static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult){
		if (!world.isClient) {
			BlockEntity e = world.getBlockEntity(hitResult.getBlockPos());
			BlockState s = world.getBlockState(hitResult.getBlockPos());
			//byPos, byBlock, byTag, byEType, byENbt
			if (e!=null || s!=null&&!s.isAir()) {
				boolean opened = DefaultInventories.getBlockUseInventories().anyMatch(i -> {
					return i.shouldOpen((ServerPlayerEntity) player, s, e) && ActionInventoryManager.open(i, (ServerPlayerEntity)player);
				});
				if (opened) {
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}
	
	private static boolean isAir(Block b) {
		return b==Blocks.AIR || b==Blocks.CAVE_AIR || b==Blocks.VOID_AIR;
	}
	
	public static ActionResult onBlockAttack(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction){
		if (!world.isClient) {
			BlockEntity e = world.getBlockEntity(pos);
			BlockState s = world.getBlockState(pos);
			if (e!=null || s!=null&&!isAir(s.getBlock())) {
				boolean opened = DefaultInventories.getBlockAttackInventories().anyMatch(i -> {
					return i.shouldOpen((ServerPlayerEntity) player, s, e, direction) && ActionInventoryManager.open(i, (ServerPlayerEntity)player);
				});
				if (opened) {
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}
}