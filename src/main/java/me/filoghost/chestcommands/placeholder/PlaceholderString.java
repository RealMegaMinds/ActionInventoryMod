/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.placeholder;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlaceholderString {

    private final String originalString;
    private final String stringWithStaticPlaceholders;
    private final boolean hasDynamicPlaceholders;

    public static @Nullable PlaceholderString of(String string) {
        if (string != null) {
            return new PlaceholderString(string);
        } else {
            return null;
        }
    }
    
    private PlaceholderString(String originalString) {
        this.originalString = originalString;
        this.stringWithStaticPlaceholders = PlaceholderManager.replaceStaticPlaceholders(originalString);
        this.hasDynamicPlaceholders = PlaceholderManager.hasDynamicPlaceholders(stringWithStaticPlaceholders);
    }
    
    public String getValue(ServerPlayerEntity player) {
        if (hasDynamicPlaceholders) {
            return PlaceholderManager.replaceDynamicPlaceholders(stringWithStaticPlaceholders, player);
        } else {
            return stringWithStaticPlaceholders;
        }
    }

    public String getOriginalValue() {
        return originalString;
    }

    public boolean hasDynamicPlaceholders() {
        return hasDynamicPlaceholders;
    }

}
