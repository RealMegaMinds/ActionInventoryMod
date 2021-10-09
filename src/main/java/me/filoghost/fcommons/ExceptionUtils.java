/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class ExceptionUtils {

    public static String getStackTraceOutput(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    public static List<String> getStackTraceOutputLines(Throwable throwable) {
        String stackTraceOutput = getStackTraceOutput(throwable);
        return Arrays.asList(stackTraceOutput.split("\\r?\\n", 0)); // "0" to remove empty trailing parts
    }

}
