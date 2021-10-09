/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Strings {

    public static boolean isEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    public static String truncate(@Nullable String string, int maxLength) {
        if (string != null && string.length() > maxLength) {
            return string.substring(0, maxLength);
        } else {
            return string;
        }
    }

    public static String[] trim(@NotNull String... strings) {
        String[] output = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            output[i] = strings[i].trim();
        }
        return output;
    }

    /**
     * Splits a string without using regular expressions, keeping empty leading and trailing strings.
     */
    public static String[] split(@NotNull String string, @NotNull String delimiter) {
        return split(string, delimiter, 0);
    }

    /**
     * Splits a string without using regular expressions, keeping empty leading and trailing strings.
     */
    public static String[] split(@NotNull String string, @NotNull String delimiter, int limit) {
        Preconditions.notNull(string, "string");
        Preconditions.notEmpty(delimiter, "delimiter");
        Preconditions.checkArgument(limit >= 0, "limit cannot be negative");

        if (string.isEmpty() || limit == 1) {
            // Optimization for trivial cases where no splits would occur
            return new String[]{string};
        }

        int firstIndex = string.indexOf(delimiter);
        if (firstIndex == -1) {
            return new String[]{string};
        }

        List<String> parts = null;
        int fromIndex = 0;
        int matchIndex;

        while ((matchIndex = string.indexOf(delimiter, fromIndex)) != -1) {
            if (parts == null) {
                parts = new ArrayList<>();
            }

            if (limit > 0 && parts.size() >= limit - 1) {
                // Limit reached (keep one slot for the remaining part)
                break;
            }

            parts.add(string.substring(fromIndex, matchIndex));
            fromIndex = matchIndex + delimiter.length();
        }

        if (parts == null || parts.isEmpty()) {
            // No match found, return the full input string
            return new String[]{string};
        }

        // Add the remaining part of the string
        parts.add(string.substring(fromIndex));

        return parts.toArray(new String[0]);
    }

    public static String[] splitAndTrim(@NotNull String string, @NotNull String delimiter) {
        return splitAndTrim(string, delimiter, 0);
    }

    public static String[] splitAndTrim(@NotNull String string, @NotNull String delimiter, int limit) {
        String[] parts = split(string, delimiter, limit);
        // Apply "trim" in place to avoid creating a new array
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }

    /**
     * @param fromIndex the index of the first element, inclusive
     */
    public static String joinFrom(@NotNull String separator, String @NotNull [] elements, int fromIndex) {
        return joinRange(separator, elements, fromIndex, elements.length);
    }

    /**
     * @param fromIndex the index of the first element, inclusive
     * @param toIndex the index of the last element, exclusive
     */
    public static String joinRange(@NotNull String separator, String @NotNull [] elements, int fromIndex, int toIndex) {
        Preconditions.notNull(separator, "separator");
        Preconditions.notNull(elements, "elements");
        Preconditions.checkArgument(fromIndex <= toIndex, "fromIndex (" + fromIndex + ") cannot be greater than toIndex (" + toIndex + ")");
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("fromIndex (" + fromIndex + ") cannot be negative");
        }
        if (toIndex > elements.length) {
            throw new ArrayIndexOutOfBoundsException("toIndex (" + toIndex + ") cannot be greater than array length (" + elements.length + ")");
        }

        StringBuilder output = new StringBuilder();

        for (int i = fromIndex; i < toIndex; i++) {
            if (output.length() != 0) {
                output.append(separator);
            }

            output.append(elements[i]);
        }

        return output.toString();
    }

    public static String stripChars(@Nullable String string, char... charsToRemove) {
        if (isEmpty(string) || charsToRemove.length == 0) {
            return string;
        }

        StringBuilder output = new StringBuilder(string.length());

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (!arrayContains(charsToRemove, c)) {
                output.append(c);
            }
        }

        return output.toString();
    }

    private static boolean arrayContains(char[] array, char valueToFind) {
        for (char c : array) {
            if (c == valueToFind) {
                return true;
            }
        }

        return false;
    }

    public static String capitalizeFully(@Nullable String string) {
        if (isEmpty(string)) {
            return string;
        }

        string = string.toLowerCase();
        int length = string.length();
        StringBuilder output = new StringBuilder(length);
        boolean capitalizeNext = true;

        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);

            if (Character.isWhitespace(c)) {
                output.append(c);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                output.append(Character.toTitleCase(c));
                capitalizeNext = false;
            } else {
                output.append(c);
            }
        }
        return output.toString();
    }

    public static String capitalizeFirst(@Nullable String string) {
        if (isEmpty(string)) {
            return string;
        }

        return Character.toTitleCase(string.charAt(0)) + string.substring(1);
    }

    public static boolean isWhitespace(@Nullable String string) {
        if (isEmpty(string)) {
            return true;
        }

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasSentenceEnding(@Nullable String string) {
        if (isEmpty(string)) {
            return false;
        }

        char lastChar = string.charAt(string.length() - 1);
        return lastChar == '.' || lastChar == '?' || lastChar == '!';
    }

    public static boolean containsIgnoreCase(@NotNull String[] array, @NotNull String target) {
        for (String element : array) {
            if (target.equalsIgnoreCase(element)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsIgnoreCase(@NotNull Collection<String> collection, @NotNull String target) {
        for (String element : collection) {
            if (target.equalsIgnoreCase(element)) {
                return true;
            }
        }
        return false;
    }

}
