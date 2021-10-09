/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Icons are the elements contained in a {@link Menu}, rendered as {@link ItemStack} when presented to a player.
 * <p>
 * Although this interface can be implemented with custom classes, it is recommended to use the provided constructors:
 * {@link ConfigurableIcon#create(Material)} and {@link StaticIcon#create(ItemStack)}.
 *
 * @since 1
 */
public interface Icon {

    /**
     * Creates an item stack to be displayed inside the menu. This method is called when a {@link MenuView} is opened or
     * refreshed by calling {@link MenuView#refresh()}.
     *
     * @param viewer the player viewing the menu
     * @return the item stack to display
     * @since 1
     */
    @Nullable ItemStack render(@NotNull ServerPlayerEntity viewer);

    /**
     * Called when a player clicks on the icon.
     *
     * @param menuView the menu view associated with the player that clicked an icon
     * @param clicker  the player that clicked an icon
     * @since 1
     */
    void onClick(@NotNull MenuView menuView, @NotNull ServerPlayerEntity clicker);

}