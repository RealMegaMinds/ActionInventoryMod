/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

public abstract class ConfigurableCommandProperties implements CommandProperties {

    private String name;
    private String permission;
    private String permissionMessage;
    private String usageArgs;
    private int minArgs;

    @Override
    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        if (this.name != null) {
            throw new IllegalStateException("can set name only once");
        }
        this.name = name;
    }

    @Override
    public final String getPermission() {
        return permission;
    }

    public final void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public final String getPermissionMessage() {
        return permissionMessage;
    }

    public final void setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
    }

    @Override
    public final String getUsageArgs() {
        return usageArgs;
    }

    public final void setUsageArgs(String usageArgs) {
        this.usageArgs = usageArgs;
    }

    @Override
    public final int getMinArgs() {
        return minArgs;
    }

    public final void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

}
