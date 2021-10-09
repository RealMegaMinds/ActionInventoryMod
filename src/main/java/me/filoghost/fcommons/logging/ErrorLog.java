/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.logging;

import com.google.common.collect.ImmutableList;

public class ErrorLog {

    private final ImmutableList<String> message;
    private final Throwable cause;

    protected ErrorLog(Throwable cause, String[] message) {
        this.message = ImmutableList.copyOf(message);
        this.cause = cause;
    }

    public ImmutableList<String> getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

}
