/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.task;

import me.filoghost.chestcommands.inventory.DefaultMenuView;
import me.filoghost.chestcommands.menu.InternalMenu;
import me.filoghost.chestcommands.menu.MenuManager;
import me.filoghost.chestcommands.placeholder.PlaceholderManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class TickingTask implements Runnable {

    private long currentTick;

    @Override
    public void run() {
        updateMenus();
        PlaceholderManager.onTick();

        currentTick++;
    }

    private void updateMenus() {
    	MinecraftServer server = null;	//TODO need to get server here
        for (String name : server.getPlayerNames()) {
        	ServerPlayerEntity player = server.getPlayerManager().getPlayer(name);
            DefaultMenuView menuView = MenuManager.getOpenMenuView(player);

            if (menuView == null || !(menuView.getMenu() instanceof InternalMenu)) {
                continue;
            }

            int refreshTicks = ((InternalMenu) menuView.getMenu()).getRefreshTicks();

            if (refreshTicks > 0 && currentTick % refreshTicks == 0) {
                menuView.refresh();
            }
        }
    }

}
