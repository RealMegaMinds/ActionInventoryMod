/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import me.filoghost.fcommons.command.validation.CommandException;
import net.minecraft.command.CommandSource;

public interface RootCommand extends CommandProperties {

    void execute(CommandSource sender, String[] args, CommandContext commandContext) throws CommandException;

}