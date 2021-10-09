/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import java.util.Arrays;

public class CommandHelper {

    public static String[] getArgsFromIndex(String[] args, int fromIndex) {
        return Arrays.copyOfRange(args, fromIndex, args.length);
    }

    public static String joinArgsFromIndex(String[] args, int fromIndex) {
        return String.join(" ", getArgsFromIndex(args, fromIndex));
    }

}
