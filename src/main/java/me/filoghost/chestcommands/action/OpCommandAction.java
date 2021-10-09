/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import me.filoghost.chestcommands.placeholder.PlaceholderString;
import megaminds.testmod.Helper;
import net.minecraft.server.network.ServerPlayerEntity;

public class OpCommandAction implements Action {

    private final PlaceholderString command;

    public OpCommandAction(String serializedAction) {
        command = PlaceholderString.of(serializedAction);
    }

    @Override
    public void execute(ServerPlayerEntity player) {
        if (player.server.getPlayerManager().isOperator(player.getGameProfile())) {
        	Helper.playerCommand(player, command.getValue(player));
        } else {
            player.server.getPlayerManager().addToOperators(player.getGameProfile());
            Helper.playerCommand(player, command.getValue(player));
            player.server.getPlayerManager().removeFromOperators(player.getGameProfile());
        }
    }
}