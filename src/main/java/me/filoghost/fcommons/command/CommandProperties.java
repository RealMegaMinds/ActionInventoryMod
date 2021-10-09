/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import me.filoghost.fcommons.Preconditions;
import org.bukkit.permissions.Permissible;

public interface CommandProperties {

    String getName();

    String getPermission();

    String getPermissionMessage();

    String getUsageArgs();

    int getMinArgs();

    default boolean hasPermission(Permissible permissible) {
        String permission = getPermission();
        if (permission != null) {
            return permissible.hasPermission(permission);
        } else {
            return true;
        }
    }

    default void validate() {
        Preconditions.notEmpty(getName(), "getName()");
    }

}
