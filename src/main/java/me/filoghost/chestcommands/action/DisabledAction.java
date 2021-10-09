/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import megaminds.testmod.Helper;
import net.minecraft.server.network.ServerPlayerEntity;

public class DisabledAction implements Action {

    private final String errorMessage;

    public DisabledAction(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void execute(ServerPlayerEntity player) {
    	Helper.serverToPlayerMessage(errorMessage, player);
    }
}