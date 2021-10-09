/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import me.filoghost.chestcommands.placeholder.PlaceholderString;
import me.filoghost.fcommons.Colors;
import megaminds.testmod.Helper;
import net.minecraft.server.network.ServerPlayerEntity;

public class BroadcastAction implements Action {
    
    private final PlaceholderString message;

    public BroadcastAction(String serializedAction) {
        message = PlaceholderString.of(Colors.colorize(serializedAction));
    }

    @Override
    public void execute(ServerPlayerEntity player) {
    	//TODO This could be playerToAll or serverToAll or nullToAll
    	Helper.playerToAllMessage(player, message.getValue(player));
    }
}