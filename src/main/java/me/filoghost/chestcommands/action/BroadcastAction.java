/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import me.filoghost.fcommons.Colors;
import megaminds.testmod.Helper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class BroadcastAction implements Action {
    
    private final TranslatableText message;

    public BroadcastAction(String serializedAction) {
        message = PlaceholderString.of(Colors.colorize(serializedAction));
    }

    @Override
    public void execute(ServerPlayerEntity player) {
    	//TODO This could be playerToAll or serverToAll or nullToAll
    	Helper.playerToAllMessage(player, message.getValue(player));
    }
}