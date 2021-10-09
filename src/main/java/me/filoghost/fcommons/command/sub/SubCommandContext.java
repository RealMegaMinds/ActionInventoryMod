/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub;

import me.filoghost.fcommons.command.CommandContext;
import org.bukkit.command.CommandSender;

public class SubCommandContext extends CommandContext {

    private final SubCommand subCommand;
    private final String subLabel;

    public SubCommandContext(CommandSender sender, String rootLabel, String[] args, SubCommand subCommand, String subLabel) {
        super(sender, rootLabel, args);
        this.subCommand = subCommand;
        this.subLabel = subLabel;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

    public String getSubLabel() {
        return subLabel;
    }

}
