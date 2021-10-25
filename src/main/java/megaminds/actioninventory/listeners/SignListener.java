package megaminds.actioninventory.listeners;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.filter.TextStream.Message;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

import megaminds.actioninventory.FileUtils;
import megaminds.actioninventory.MessageHelper;
import megaminds.actioninventory.inventory.ActionInventory;
import megaminds.actioninventory.inventory.ActionManager;
import megaminds.actioninventory.inventory.openers.Opener.ClickType;
import megaminds.actioninventory.inventory.openers.Opener.What;

public class SignListener {

	private static final int HEADER_LINE = 0;
	private static final int NAME_LINE = 1;

	private static final String SIGN_CREATION_TRIGGER = "[ActionInventory]";

	private static final TextColor VALID_SIGN_COLOR = TextColor.fromFormatting(Formatting.DARK_BLUE);

	public static ActionResult onSignAttack(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction dir) {
		return onSignClick(player, world, pos, ClickType.ATTACK);
	}
	public static ActionResult onSignUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		return onSignClick(player, world, hitResult.getBlockPos(), ClickType.USE);
	}
	private static ActionResult onSignClick(PlayerEntity inPlayer, World world, BlockPos pos, ClickType click) {
		if (world.isClient) return ActionResult.PASS;
		
		ServerPlayerEntity player = (ServerPlayerEntity) inPlayer;
		BlockEntity block = world.getBlockEntity(pos);
		if (block!=null && block instanceof SignBlockEntity) {
			SignBlockEntity sign = (SignBlockEntity) block;
			if (isValidSignHeader(sign.getTextOnRow(HEADER_LINE, false))) {
				String invName = FileUtils.stripExtension(sign.getTextOnRow(NAME_LINE, false).asString().trim());
				ActionInventory inv = ActionManager.getInventory(invName);
				if (inv==null) {
					MessageHelper.toPlayerMessage(player, MessageHelper.toError("No Action Inventory Called: "+invName), true);
					return ActionResult.FAIL;
				} else if (ActionManager.notify(inv, player, click, What.Sign, null)) {
					return ActionResult.SUCCESS;
				} else {
					MessageHelper.toPlayerMessage(player, MessageHelper.toError("Cannot Open Action Inventory Called: "+invName), true);
					return ActionResult.FAIL;
				}
			}
		}
		return ActionResult.PASS;
	}

	public static void onSignChange(UpdateSignC2SPacket packet, List<Message> messages, ServerPlayerEntity player) {
		SignBlockEntity sign = (SignBlockEntity) player.getServerWorld().getBlockEntity(packet.getPos());
		Text header = sign.getTextOnRow(HEADER_LINE, false);
		
		if (isValidTrigger(header)) {
			if (player.hasPermissionLevel(4)) {
				String invName = sign.getTextOnRow(NAME_LINE, false).asString().strip();

				if (invName.isEmpty()) {
					MessageHelper.toPlayerMessage(player, MessageHelper.toError("You must write a menu name in the second line."), true);
					return;
				}

				invName = FileUtils.stripExtension(invName);

				ActionInventory menu = ActionManager.getInventory(invName);
				if (menu == null) {
					MessageHelper.toPlayerMessage(player, MessageHelper.toError("No Action Inventory Called: "+invName), true);
					return;
				}

				sign.setTextOnRow(HEADER_LINE, getValidHeader(), sign.getTextOnRow(HEADER_LINE, true));
				MessageHelper.toPlayerMessage(player, MessageHelper.toSuccess("Sign Created For: "+invName), true);
			} else if (isValidSignColor(header)) {
				// Prevent players without permission from creating ActionInventory signs
				sign.setTextOnRow(HEADER_LINE, header.shallowCopy().setStyle(header.getStyle().withColor((Formatting)null)));
			}
		}
	}
	
	private static Text getValidHeader() {
		return new LiteralText(SIGN_CREATION_TRIGGER).setStyle(Style.EMPTY.withColor(VALID_SIGN_COLOR));
	}

	private static boolean isValidSignColor(Text header) {
		return VALID_SIGN_COLOR.equals(header.getStyle().getColor());
	}

	private static boolean isValidTrigger(Text headerLine) {
		return headerLine.asString().equalsIgnoreCase(SIGN_CREATION_TRIGGER);
	}

	private static boolean isValidSignHeader(Text header) {
		return isValidSignColor(header) && isValidTrigger(header);
	}
}