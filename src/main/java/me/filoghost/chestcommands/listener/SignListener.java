/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.listener;

import me.filoghost.chestcommands.Permissions;
import me.filoghost.chestcommands.config.Lang;
import me.filoghost.chestcommands.menu.InternalMenu;
import me.filoghost.chestcommands.menu.MenuManager;
import me.filoghost.chestcommands.util.Utils;
import megaminds.testmod.Helper;
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
import net.minecraft.world.World;

import java.util.List;

public class SignListener {

	private static final int HEADER_LINE = 0;
	private static final int FILENAME_LINE = 1;

	private static final String SIGN_CREATION_TRIGGER = "[menu]";

	private static final TextColor VALID_SIGN_COLOR = TextColor.fromFormatting(Formatting.DARK_BLUE);

	public static ActionResult onSignClick(PlayerEntity inPlayer, World world, Hand hand, BlockHitResult hitResult) {
		if (world.isClient) return ActionResult.PASS;
		ServerPlayerEntity player = (ServerPlayerEntity) inPlayer;
		
		BlockEntity block = world.getBlockEntity(hitResult.getBlockPos());
		if (block instanceof SignBlockEntity) {
			SignBlockEntity sign = (SignBlockEntity) block;
			if (isValidSignHeader(sign.getTextOnRow(HEADER_LINE, false))) {
				String menuFileName = Utils.addYamlExtension(sign.getTextOnRow(FILENAME_LINE, false).asString().trim());
				InternalMenu menu = MenuManager.getMenuByFileName(menuFileName);
				if (menu == null) {
					Helper.serverToPlayerMessage(Lang.get().menu_not_found, player);
				} else if (menu.openCheckingPermission(player)) {
					return ActionResult.SUCCESS;
				}
				return ActionResult.FAIL;
			}
		}
		return ActionResult.PASS;
	}

	public static void onSignChange(UpdateSignC2SPacket packet, List<Message> messages, ServerPlayerEntity player) {
		SignBlockEntity sign = (SignBlockEntity) player.getServerWorld().getBlockEntity(packet.getPos());
		Text header = sign.getTextOnRow(HEADER_LINE, false);
		
		if (isValidTrigger(header)) {
			if (canCreateMenuSign(player)) {
				String menuFileName = sign.getTextOnRow(FILENAME_LINE, false).asString().strip();

				if (menuFileName.isEmpty()) {
					Helper.serverToPlayerMessage(new LiteralText("You must write a menu name in the second line.").setStyle(Style.EMPTY.withColor(Formatting.RED)), player);
					return;
				}

				menuFileName = Utils.addYamlExtension(menuFileName);

				InternalMenu menu = MenuManager.getMenuByFileName(menuFileName);
				if (menu == null) {
					Helper.serverToPlayerMessage(new LiteralText("Menu \"" + menuFileName + "\" was not found.").setStyle(Style.EMPTY.withColor(Formatting.RED)), player);
					return;
				}

				sign.setTextOnRow(HEADER_LINE, getValidHeader(), sign.getTextOnRow(HEADER_LINE, true));
				Helper.serverToPlayerMessage(new LiteralText("Successfully created a sign for the menu " + menuFileName + ".").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), player);
			} else if (isValidSignColor(header)) {
				// Prevent players without permission from creating menu signs
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

	private static boolean canCreateMenuSign(ServerPlayerEntity player) {
		return player.hasPermissionLevel(Permissions.SIGN_CREATE);
	}
}