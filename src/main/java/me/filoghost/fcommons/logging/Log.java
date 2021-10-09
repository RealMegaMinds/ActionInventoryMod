/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.logging;

import me.filoghost.fcommons.FCommons;

import java.util.logging.Level;

public class Log {

    public static void info(String msg) {
        info(msg, null);
    }

    public static void info(String msg, Throwable thrown) {
        log(Level.INFO, msg, thrown);
    }

    public static void warning(String msg) {
        warning(msg, null);
    }

    public static void warning(String msg, Throwable thrown) {
        log(Level.WARNING, msg, thrown);
    }

    public static void severe(String msg) {
        severe(msg, null);
    }

    public static void severe(String msg, Throwable thrown) {
        log(Level.SEVERE, msg, thrown);
    }

    private static void log(Level level, String msg, Throwable thrown) {
        FCommons.getLogger().log(level, msg, thrown);
    }
}