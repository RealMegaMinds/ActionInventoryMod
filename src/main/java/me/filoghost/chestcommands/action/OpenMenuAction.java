/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.menu.InternalMenu;
import me.filoghost.chestcommands.menu.MenuManager;
import me.filoghost.chestcommands.placeholder.PlaceholderString;
import megaminds.testmod.Helper;
import net.minecraft.server.network.ServerPlayerEntity;

public class OpenMenuAction implements Action {

	private final PlaceholderString targetMenu;

	public OpenMenuAction(String serializedAction) {
		targetMenu = PlaceholderString.of(serializedAction);
	}

	@Override
	public void execute(final ServerPlayerEntity player) {
		String menuName = targetMenu.getValue(player);
		final InternalMenu menu = MenuManager.getMenuByFileName(menuName);

		if (menu != null) {
			menu.openCheckingPermission(player);
		} else {
			Helper.serverToPlayerMessage(Errors.User.configurationError("couldn't find the menu \"" + menuName + "\""), player);
		}
	}
}