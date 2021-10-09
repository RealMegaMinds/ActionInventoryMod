/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub.annotated;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.command.sub.SubCommand;
import me.filoghost.fcommons.command.sub.SubCommandManager;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class AnnotatedSubCommandManager extends SubCommandManager {

    private final SortedSet<AnnotatedSubCommand> subCommands;

    public AnnotatedSubCommandManager() {
        this.subCommands = new TreeSet<>(Comparator
                .comparing(AnnotatedSubCommand::getDisplayPriority).reversed()
                .thenComparing(SubCommand::getName, String.CASE_INSENSITIVE_ORDER));

        scanMethodsSubCommands(getClass());
    }

    private void scanMethodsSubCommands(Class<?> classToScan) {
        for (Method method : classToScan.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Name.class)) {
                AnnotatedSubCommand subCommand = new AnnotatedMethodSubCommand(this, method);
                registerSubCommand(subCommand);
            }
        }
    }

    @Override
    protected final AnnotatedSubCommand getSubCommandByName(String name) {
        for (AnnotatedSubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(name)) {
                return subCommand;
            }
        }
        return null;
    }

    @Override
    protected final Iterable<AnnotatedSubCommand> getSubCommands() {
        return subCommands;
    }

    protected final void registerSubCommand(AnnotatedSubCommand subCommand) {
        Preconditions.notNull(subCommand, "subCommand");
        subCommand.validate();
        Preconditions.checkState(getSubCommandByName(subCommand.getName()) == null,
                "subCommand with same name already registered");

        if (subCommand.getPermission() == null) {
            subCommand.setPermission(getDefaultSubCommandPermission(subCommand));
        }

        subCommands.add(subCommand);
    }

    protected String getDefaultSubCommandPermission(AnnotatedSubCommand subCommand) {
        return null;
    }

}
