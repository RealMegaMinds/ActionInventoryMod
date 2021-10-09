/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import me.filoghost.chestcommands.placeholder.PlaceholderString;
import megaminds.testmod.Helper;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerCommandAction implements Action {

    private final PlaceholderString command;

    public PlayerCommandAction(String serializedAction) {
        command = PlaceholderString.of(serializedAction);
    }

    @Override
    public void execute(ServerPlayerEntity player) {
    	Helper.playerCommand(player, command.getValue(player));
    }
}