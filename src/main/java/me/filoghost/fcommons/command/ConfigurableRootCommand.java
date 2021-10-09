/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import me.filoghost.fcommons.command.validation.CommandException;
import net.minecraft.command.CommandSource;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public abstract class ConfigurableRootCommand extends ConfigurableCommandProperties implements RootCommand {

    public final boolean register(JavaPlugin plugin) {
        super.validate();

        PluginCommand pluginCommand = plugin.getCommand(getName());
        if (pluginCommand == null) {
            return false;
        }

        if (getPermission() != null) {
            pluginCommand.setPermission(getPermission());
        }
        if (getPermissionMessage() != null) {
            pluginCommand.setPermissionMessage(getPermissionMessage());
        } else {
            pluginCommand.setPermissionMessage(ChatColor.RED + "You don't have permission for this command.");
        }

        pluginCommand.setExecutor(new BukkitCommandExecutorAdapter(this));
        return true;
    }

    protected void handleUnexpectedException(CommandContext context, Throwable t) {
        Bukkit.getLogger().log(Level.SEVERE, "Internal error while executing /" + context.getRootLabel(), t);
        context.getSender().sendMessage(ChatColor.RED + "Internal error while executing command.");
    }

    protected void sendExecutionErrorMessage(CommandContext context, String errorMessage) {
        context.getSender().sendMessage(ChatColor.RED + errorMessage);
    }


    private static class BukkitCommandExecutorAdapter implements CommandExecutor {

        private final ConfigurableRootCommand command;

        private BukkitCommandExecutorAdapter(ConfigurableRootCommand command) {
            this.command = command;
        }

        @Override
        public final boolean onCommand(CommandSource sender, Command bukkitCommand, String label, String[] args) {
            CommandContext context = new CommandContext(sender, label, args);

            try {
                command.execute(context.getSender(), context.getArgs(), context);
            } catch (CommandException ex) {
                command.sendExecutionErrorMessage(context, ex.getMessage());
            } catch (Throwable t) {
                command.handleUnexpectedException(context, t);
            }
            return true;
        }

    }

}
