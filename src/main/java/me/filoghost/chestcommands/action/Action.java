/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import net.minecraft.server.network.ServerPlayerEntity;

public interface Action {
    void execute(ServerPlayerEntity player);
}