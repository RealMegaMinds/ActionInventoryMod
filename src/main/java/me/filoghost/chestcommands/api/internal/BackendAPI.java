/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.api.internal;

import me.filoghost.chestcommands.api.ConfigurableIcon;
import me.filoghost.chestcommands.api.Menu;
import me.filoghost.chestcommands.api.PlaceholderReplacer;
import me.filoghost.chestcommands.api.StaticIcon;
import me.filoghost.fcommons.Preconditions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Do not use this class: it is intended only for internal use and may change at any time.
 */
@ApiStatus.Internal
public abstract class BackendAPI {
    
    private static BackendAPI implementation;
    
    public static void setImplementation(@NotNull BackendAPI implementation) {
        Preconditions.notNull(implementation, "implementation");
        Preconditions.checkState(BackendAPI.implementation == null, "implementation already set");

        BackendAPI.implementation = implementation;
    }
    
    public static @NotNull BackendAPI getImplementation() {
        Preconditions.checkState(implementation != null, "no implementation set");
        
        return implementation;
    }
    
    public abstract boolean isInternalMenuLoaded(@NotNull String menuFileName);

    public abstract boolean openInternalMenu(@NotNull ServerPlayerEntity player, @NotNull String menuFileName);

    public abstract @NotNull Menu createMenu(@NotNull String title, int rows);
    
    public abstract @NotNull ConfigurableIcon createConfigurableIcon(@NotNull Item material);

    public abstract @NotNull StaticIcon createStaticIcon(@NotNull ItemStack itemStack);

    public abstract void registerPlaceholder(@NotNull String identifier,
                                             @NotNull PlaceholderReplacer placeholderReplacer);

    public abstract boolean unregisterPlaceholder(@NotNull String identifier);

}
