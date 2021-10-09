/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.validation;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandValidate {

    public static void notNull(Object object, String errorMessage) throws CommandException {
        if (object == null) {
            throw new CommandException(errorMessage);
        }
    }

    public static void check(boolean expression, String errorMessage) throws CommandException {
        if (!expression) {
            throw new CommandException(errorMessage);
        }
    }

    public static void minLength(Object[] array, int minLength, String errorMessage) throws CommandException {
        if (array.length < minLength) {
            throw new CommandException(errorMessage);
        }
    }

    public static Player getPlayerSender(CommandSender sender) throws CommandException {
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            throw new CommandException("You must be a player to use this command.");
        }
    }

    public static int parseInteger(String input) throws CommandException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new CommandException("Invalid integer number \"" + input + "\".");
        }
    }

    public static int parseIntegerZeroOrPositive(String input) throws CommandException {
        int number = parseInteger(input);
        if (number < 0) {
            throw new CommandException("Number \"" + input + "\" must be 0 or greater.");
        }
        return number;
    }

    public static int parseIntegerStrictlyPositive(String input) throws CommandException {
        int number = parseInteger(input);
        if (number <= 0) {
            throw new CommandException("Number \"" + input + "\" must be greater than 0.");
        }
        return number;
    }

    public static double parseDouble(String input) throws CommandException {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new CommandException("Invalid decimal number \"" + input + "\".");
        }
    }

    public static double parseDoubleZeroOrPositive(String input) throws CommandException {
        double number = parseDouble(input);
        if (number < 0.0) {
            throw new CommandException("Number \"" + input + "\" must be 0 or greater.");
        }
        return number;
    }

    public static double parseDoubleStrictlyPositive(String input) throws CommandException {
        double number = parseDouble(input);
        if (number <= 0.0) {
            throw new CommandException("Number \"" + input + "\" must be greater than 0.");
        }
        return number;
    }

}
