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
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import org.bukkit.event.block.Action;

public class SignListener {

	private static final int HEADER_LINE = 0;
	private static final int FILENAME_LINE = 1;

	private static final String SIGN_CREATION_TRIGGER = "[menu]";

	private static final TextColor VALID_SIGN_COLOR = TextColor.fromFormatting(Formatting.DARK_BLUE);
	
	private static boolean isValidSignHeader(Text header) {
		return VALID_SIGN_COLOR.equals(header.getStyle().getColor()) && isValidTrigger(header);
	}
	
	public static ActionResult onSignClick(ServerPlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
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
	
	public static void onSignChange() {
		//TODO this
	}

	public void onCreateMenuSign(SignChangeEvent event) {
		Player player = event.getPlayer();

		if (isCreatingMenuSign(event.getLine(HEADER_LINE)) && canCreateMenuSign(player)) {
			String menuFileName = event.getLine(FILENAME_LINE).trim();

			if (menuFileName.isEmpty()) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You must write a menu name in the second line.");
				return;
			}

			menuFileName = Utils.addYamlExtension(menuFileName);

			InternalMenu menu = MenuManager.getMenuByFileName(menuFileName);
			if (menu == null) {
				event.setCancelled(true);
				player.sendMessage(new LiteralText("Menu \"" + menuFileName + "\" was not found.").setStyle(Style.EMPTY.withColor(Formatting.RED)));
				return;
			}

			event.setLine(HEADER_LINE, VALID_SIGN_COLOR + event.getLine(HEADER_LINE));
			player.sendMessage(new LiteralText("Successfully created a sign for the menu " + menuFileName + ".").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
		}
	}



	public static void onSignChangeMonitor(SignBlockEntity sign, ServerPlayerEntity player) {
		// Prevent players without permissions from creating menu signs
		Text header = sign.getTextOnRow(HEADER_LINE, false);
		if (isValidSignHeader(header) && !canCreateMenuSign(player)) {
			sign.setTextOnRow(HEADER_LINE, header.shallowCopy().setStyle(header.getStyle().withColor((Formatting)null)));
		}
	}

	private static boolean isValidTrigger(Text headerLine) {
		return headerLine.asString().equalsIgnoreCase(SIGN_CREATION_TRIGGER);
	}

	private static boolean canCreateMenuSign(ServerPlayerEntity player) {
		return player.hasPermissionLevel(Permissions.SIGN_CREATE);
	}

}
