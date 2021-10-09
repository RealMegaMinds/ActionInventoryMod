/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.validation;

import me.filoghost.fcommons.Preconditions;

public class CommandException extends Exception {

    public CommandException(String message) {
        super(message);
        Preconditions.notEmpty(message, "message");
    }

}
