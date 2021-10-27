package megaminds.actioninventory.listeners;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Text.Serializer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import megaminds.actioninventory.FileUtils;
import megaminds.actioninventory.MessageHelper;
import megaminds.actioninventory.commands.Commands;
import megaminds.actioninventory.inventory.helpers.ActionManager;

public class SignListener {

	private static final int HEADER_LINE = 0;
	private static final int NAME_LINE = 1;

	private static final String VALID_HEADER = "[ActionInventory]";

	public static ActionResult onSignBlockAttack(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction dir) {
		if (world.isClient || player.hasPermissionLevel(4)) return ActionResult.PASS;

		BlockEntity blockEntity = world.getBlockEntity(pos);
		Block block = world.getBlockState(pos).getBlock();
		if (blockEntity==null || !(blockEntity instanceof SignBlockEntity) || !(block instanceof AbstractSignBlock)) return ActionResult.PASS;

		SignBlockEntity sign = (SignBlockEntity)blockEntity;
		if (!isValidHeader(sign.getTextOnRow(HEADER_LINE, false))) return ActionResult.PASS;

		String name = isValidName(sign.getTextOnRow(NAME_LINE, false));
		if (name==null) return ActionResult.PASS;

		ItemStack signStack = new ItemStack(Items.STICK);
		signStack.setCustomName(new LiteralText(VALID_HEADER));
		signStack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).putString(ItemStack.LORE_KEY, name);
		player.getInventory().offerOrDrop(signStack);

		return ActionResult.PASS;
	}

	public static ActionResult onSignBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());
			if (blockEntity!=null && blockEntity instanceof SignBlockEntity) {
				ItemStack stack = player.getStackInHand(hand);
				NbtCompound display = stack.getSubNbt(ItemStack.DISPLAY_KEY);
				if (stack.hasCustomName() && isValidHeader(Serializer.fromJson(display.getString(ItemStack.NAME_KEY)))) {
					String name = isValidName(Serializer.fromJson(display.getString(ItemStack.LORE_KEY)));
					if (name != null) {
						SignBlockEntity sign = (SignBlockEntity)blockEntity;
						ClickEvent clickEvent = new ClickEvent(Action.RUN_COMMAND, Commands.COMMAND_START+" "+name);
						boolean success = false;
						for (int i = 0; i < 4; i++) {
							Text raw = sign.getTextOnRow(i, false);
							Text filtered = sign.getTextOnRow(i, true);
							if (raw instanceof MutableText && filtered instanceof MutableText) {
								Style rStyle = raw.getStyle();
								Style fStyle = filtered.getStyle();
								if (rStyle.getClickEvent() == null && fStyle.getClickEvent()==null) {
									((MutableText)raw).setStyle(rStyle.withClickEvent(clickEvent));
									((MutableText)filtered).setStyle(fStyle.withClickEvent(clickEvent));
									success = true;
									break;
								}
							}
						}
						if (success) {
							MessageHelper.toPlayerMessage((ServerPlayerEntity)player, MessageHelper.toSuccess("Applied click event to this sign."), true);
							return ActionResult.SUCCESS;
						} else {
							MessageHelper.toPlayerMessage((ServerPlayerEntity)player, MessageHelper.toError("Cannot apply click event to this sign."), true);
							return ActionResult.FAIL;
						}
					}
				}
			}
		}
		return ActionResult.PASS;
	}

	private static String isValidName(Text name) {
		String invName = FileUtils.stripExtension(name.asString());
		return ActionManager.getInventory(invName) == null ? null : invName;
	}

	private static boolean isValidHeader(Text headerLine) {
		return headerLine.asString().equalsIgnoreCase(VALID_HEADER);
	}
}