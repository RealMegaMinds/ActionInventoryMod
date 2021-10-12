/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.logging;

import me.filoghost.chestcommands.ChestCommands;
import me.filoghost.chestcommands.legacy.UpgradeExecutorException;
import me.filoghost.chestcommands.legacy.upgrade.UpgradeTaskException;
import me.filoghost.chestcommands.parsing.ParseException;
import me.filoghost.fcommons.ExceptionUtils;
import me.filoghost.fcommons.config.exception.ConfigException;
import me.filoghost.fcommons.config.exception.ConfigSyntaxException;
import me.filoghost.fcommons.logging.ErrorCollector;
import me.filoghost.fcommons.logging.ErrorLog;
import megaminds.testmod.Helper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class PrintableErrorCollector extends ErrorCollector {


    @Override
    public void logToConsole(MinecraftServer server) {
        LiteralText output = new LiteralText("");

        if (errors.size() > 0) {
            output.append(ChestCommands.getChatPrefix()).append(new LiteralText("Encountered "+errors.size()+" error(s) on load:\n").setStyle(Style.EMPTY.withColor(Formatting.RED)));

            int index = 1;
            for (ErrorLog error : errors) {
                ErrorPrintInfo printFormat = getErrorPrintInfo(index, error);
                printError(output, printFormat);
                index++;
            }
        }

        Helper.nullToServerMessage(output, server);
    }

    private ErrorPrintInfo getErrorPrintInfo(int index, ErrorLog error) {
        List<String> message = new ArrayList<>(error.getMessage().asList());
        String details = null;
        Throwable cause = error.getCause();

        // Recursively inspect the cause until an unknown or null exception is found
        while (true) {
            if (cause instanceof ConfigSyntaxException) {
                message.add(cause.getMessage());
                details = ((ConfigSyntaxException) cause).getSyntaxErrorDetails();
                cause = null; // Do not print stacktrace for syntax exceptions

            } else if (cause instanceof ConfigException
                    || cause instanceof ParseException
                    || cause instanceof UpgradeTaskException
                    || cause instanceof UpgradeExecutorException) {
                message.add(cause.getMessage());
                cause = cause.getCause(); // Print the cause (or nothing if null), not our "known" exception

            } else {
                return new ErrorPrintInfo(index, message, details, cause);
            }
        }
    }

    private static void printError(MutableText output, ErrorPrintInfo error) {
        output.append(new LiteralText(error.getIndex()+") ").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
        output.append(new LiteralText(MessagePartJoiner.join(error.getMessage())).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));

        if (error.getDetails() != null) {
            output.append(new LiteralText(". Details:\n").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
            output.append(new LiteralText(error.getDetails()).append("\n").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
        } else {
            output.append(new LiteralText(".\n").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        }
        if (error.getCause() != null) {
            output.append(new LiteralText("--------[ Exception details ]--------\n"
            								+ExceptionUtils.getStackTraceOutput(error.getCause())
            								+"-------------------------------------\n")
            				.setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
        }
        output.append("\n");
    }
}