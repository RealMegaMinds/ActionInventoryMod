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
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class JoinListener {
	public static void onJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		ServerPlayerEntity player = handler.getPlayer();

		if (ChestCommands.getLastLoadErrors().hasErrors() && player.hasPermissionLevel(Permissions.SEE_ERRORS)) {
			Text t = ChestCommands.getChatPrefix().append(new LiteralText("The plugin found " + ChestCommands.getLastLoadErrors().getErrorsCount()+ " error(s) last time it was loaded. You can see them by doing \"/cc reload\" in the console.").setStyle(Style.EMPTY.withColor(Formatting.RED)));
			Helper.serverToPlayerMessage(t, player);
		}

		//TODO this update section needs to be changed and might be able to just be removed
		if (ChestCommands.hasNewVersion() && Settings.get().update_notifications && player.hasPermissionLevel(Permissions.UPDATE_NOTIFICATIONS)) {
			Text t = ChestCommands.getChatPrefix().append(new LiteralText("Found an update: " + ChestCommands.getNewVersion() + ". Download:").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
			Helper.serverToPlayerMessage(t, player);
			Text t2 = new LiteralText(">> ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)).append(new LiteralText("http://dev.bukkit.org/bukkit-plugins/chest-commands").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://dev.bukkit.org/bukkit-plugins/chest-commands"))));
			Helper.serverToPlayerMessage(t2, player);
		}
	}
}