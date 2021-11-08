package megaminds.actioninventory.listeners;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text.Serializer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import megaminds.actioninventory.MessageHelper;
import megaminds.actioninventory.SpecialSign;
import megaminds.actioninventory.inventory.helpers.ActionManager;

public class SignListener {
	public static ActionResult onSignBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		//This only does things on server side
		if (!world.isClient) {
			//get the BlockEntity at the hit position and make sure that it is a SpecialSign
			BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());
			if (blockEntity!=null && blockEntity instanceof SpecialSign) {
				SpecialSign sign = (SpecialSign)blockEntity;
				ItemStack stack = player.getStackInHand(hand);
				ServerPlayerEntity p = (ServerPlayerEntity)player;

				//if the player is not holding anything and the sign is actively special, try opening the ActionInventory
				if (stack.isEmpty() && sign.isSpecialSign()) {
					if (ActionManager.onSign(p, sign.getSpecial())) {
						return ActionResult.SUCCESS;
					} else {
						return ActionResult.FAIL;
					}
				} else {	//if the player is holding something, make sure it has the correct lore
					NbtCompound display = stack.getSubNbt(ItemStack.DISPLAY_KEY);
					if (display!=null && Serializer.fromJson(((NbtList)display.get(ItemStack.LORE_KEY)).getString(0)).asString().equals("ActionInventory Maker")) {
						//if it has the correct lore, make sure the name works
						String name = Serializer.fromJson(display.getString(ItemStack.NAME_KEY)).asString();
						if (ActionManager.getInventory(name) != null) {
							//if the sign is not already special, make it special and tell player
							if (!sign.isSpecialSign()) {
								sign.setSpecial(name);
								MessageHelper.toPlayerMessage(p, MessageHelper.toSuccess("Applied ActionInventory to this sign."), true);
								return ActionResult.SUCCESS;
							} else {	//if sign is already special, tell player
								MessageHelper.toPlayerMessage(p, MessageHelper.toError("An ActionInventory is already applied to this sign."), true);
								return ActionResult.FAIL;
							}
						}
					}
				}
			}
		}
		return ActionResult.PASS;
	}
}