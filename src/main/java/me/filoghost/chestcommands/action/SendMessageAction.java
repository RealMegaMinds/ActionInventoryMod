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

public class SendMessageAction implements Action {
    
    private final PlaceholderString message;

    public SendMessageAction(String serializedAction) {
        message = PlaceholderString.of(Colors.colorize(serializedAction));
    }

    @Override
    public void execute(ServerPlayerEntity player) {
    	//TODO not sure if from null or server
    	Helper.nullToPlayerMessage(message.getValue(player), player);
    }
}