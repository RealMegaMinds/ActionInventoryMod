/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import me.filoghost.chestcommands.placeholder.PlaceholderString;
import megaminds.testmod.Helper;
import net.minecraft.server.network.ServerPlayerEntity;

public class ConsoleCommandAction implements Action {

    private final PlaceholderString command;

    public ConsoleCommandAction(String serializedAction) {
        command = PlaceholderString.of(serializedAction);
    }

    @Override
    public void execute(ServerPlayerEntity player) {
    	Helper.serverCommand(player.getServer(), command.getValue(player));
    }
}