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
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import org.bukkit.ChatColor;
import org.bukkit.event.block.Action;

public class SignListener {

	private static final int HEADER_LINE = 0;
	private static final int FILENAME_LINE = 1;

	private static final String SIGN_CREATION_TRIGGER = "[menu]";

	private static final ChatColor VALID_SIGN_COLOR = ChatColor.DARK_BLUE;
	private static final String VALID_SIGN_HEADER = VALID_SIGN_COLOR + SIGN_CREATION_TRIGGER;

	public static ActionResult onSignClick(ServerPlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		BlockEntity block = world.getBlockEntity(hitResult.getBlockPos());
		if (block.getType()==BlockEntityType.SIGN) {
			SignBlockEntity sign = (SignBlockEntity) block;
			if (sign.getTextOnRow(HEADER_LINE, false).asString().equalsIgnoreCase(VALID_SIGN_HEADER)) {
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
				player.sendMessage(ChatColor.RED + "Menu \"" + menuFileName + "\" was not found.");
				return;
			}

			event.setLine(HEADER_LINE, VALID_SIGN_COLOR + event.getLine(HEADER_LINE));
			player.sendMessage(ChatColor.GREEN + "Successfully created a sign for the menu " + menuFileName + ".");
		}
	}



	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignChangeMonitor(SignChangeEvent event) {
		// Prevent players without permissions from creating menu signs
		if (isValidMenuSign(event.getLine(HEADER_LINE)) && !canCreateMenuSign(event.getPlayer())) {
			event.setLine(HEADER_LINE, ChatColor.stripColor(event.getLine(HEADER_LINE)));
		}
	}

	private boolean isCreatingMenuSign(String headerLine) {
		return headerLine.equalsIgnoreCase(SIGN_CREATION_TRIGGER);
	}

	private boolean isValidMenuSign(String headerLine) {
		return headerLine.equalsIgnoreCase(VALID_SIGN_HEADER);
	}

	private boolean canCreateMenuSign(Player player) {
		return player.hasPermission(Permissions.SIGN_CREATE);
	}

}
