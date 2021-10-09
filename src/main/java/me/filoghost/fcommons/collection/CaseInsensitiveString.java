/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import me.filoghost.fcommons.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Case insensitive string for use inside collections.
 */
public final class CaseInsensitiveString {

    private final String originalString;
    private final String uppercaseString;

    public CaseInsensitiveString(@NotNull String string) {
        Preconditions.notNull(string, "string");
        this.originalString = string;
        this.uppercaseString = string.toUpperCase(Locale.ROOT);
    }

    public String getOriginalString() {
        return originalString;
    }

    public boolean equalsIgnoreCase(String other) {
        return other != null && other.toUpperCase(Locale.ROOT).equals(this.uppercaseString);
    }

    @Override
    public String toString() {
        return originalString;
    }

    @Override
    public int hashCode() {
        return uppercaseString.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }

        return ((CaseInsensitiveString) other).uppercaseString.equals(this.uppercaseString);
    }

}
