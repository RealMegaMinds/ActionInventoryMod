/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.logging;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class to collect all the errors found while loading a plugin.
 */
public abstract class ErrorCollector {

    protected final List<ErrorLog> errors = new ArrayList<>();

    public void add(String... messageParts) {
        add(null, messageParts);
    }

    public void add(Throwable cause, String... messageParts) {
        errors.add(new ErrorLog(cause, messageParts));
    }

    public int getErrorsCount() {
        return errors.size();
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public abstract void logToConsole();

}