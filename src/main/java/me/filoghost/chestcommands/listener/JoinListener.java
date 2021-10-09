/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.listener;

import me.filoghost.chestcommands.ChestCommands;
import me.filoghost.chestcommands.Permissions;
import me.filoghost.chestcommands.config.Settings;
import megaminds.testmod.Helper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import org.bukkit.ChatColor;

public class JoinListener {
	public static void onJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		ServerPlayerEntity player = handler.getPlayer();

		if (ChestCommands.getLastLoadErrors().hasErrors() && player.hasPermissionLevel(Permissions.SEE_ERRORS)) {
			Helper.serverToPlayerMessage(ChestCommands.CHAT_PREFIX + ChatColor.RED +
					"The plugin found " + ChestCommands.getLastLoadErrors().getErrorsCount()
					+ " error(s) last time it was loaded. You can see them by doing \"/cc reload\" in the console.", player);
		}

		if (ChestCommands.hasNewVersion() && Settings.get().update_notifications && player.hasPermissionLevel(Permissions.UPDATE_NOTIFICATIONS)) {
			Helper.serverToPlayerMessage(ChestCommands.CHAT_PREFIX + "Found an update: " + ChestCommands.getNewVersion() + ". Download:", player);
			Helper.serverToPlayerMessage(ChatColor.DARK_GREEN + ">> " + ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/chest-commands", player);
		}
	}
}