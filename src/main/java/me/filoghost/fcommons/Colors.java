/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

public final class Colors {

    public static final char ALT_COLOR_CHAR = '&';
    private static final CharArray ALT_COLOR_CODES = new CharArray("0123456789AaBbCcDdEeFfKkLlMmNnOoRr");
    private static final CharArray ALT_HEX_CODES = new CharArray("0123456789AaBbCcDdEeFf");
    private static final int ALT_HEX_COLOR_LENGTH = 6;

    private static final CharArray STANDARD_COLOR_CODES = new CharArray("0123456789abcdefklmnor");
    private static final CharArray STANDARD_HEX_CODES = new CharArray("0123456789abcdef");
    private static final int STANDARD_HEX_COLOR_LENGTH = ALT_HEX_COLOR_LENGTH * 2; // Double because of the extra color chars

    /**
     * Replaces alternate color codes with standard chat color codes.
     */
    public static String colorize(@Nullable String string) {
        if (Strings.isEmpty(string) || string.indexOf(ALT_COLOR_CHAR) < 0) {
            return string;
        }

        StringBuilder result = new StringBuilder(string.length());

        int i = 0;
        while (i < string.length()) {
            char currentChar = string.charAt(i);

            if (currentChar == ALT_COLOR_CHAR && i + 1 < string.length()) {
                char nextChar = string.charAt(i + 1);

                if (nextChar == '#' && FeatureSupport.HEX_CHAT_COLORS && isAltHexColor(string, i + 2)) {
                    result.append(ChatColor.COLOR_CHAR);
                    result.append('x');
                    translateAltHexColor(string, i + 2, result);

                    i += 2 + ALT_HEX_COLOR_LENGTH; // Skip prefix and hex string
                    continue;
                }

                if (ALT_COLOR_CODES.contains(nextChar)) {
                    result.append(ChatColor.COLOR_CHAR);
                    result.append(Character.toLowerCase(nextChar));

                    i += 2; // Skip color char and color code
                    continue;
                }
            }

            // Normal char
            result.append(currentChar);
            i++;
        }

        return result.toString();
    }

    private static boolean isAltHexColor(String string, int beginIndex) {
        if (string.length() - beginIndex < ALT_HEX_COLOR_LENGTH) {
            return false;
        }

        for (int i = 0; i < ALT_HEX_COLOR_LENGTH; i++) {
            char hexCode = string.charAt(beginIndex + i);
            if (!ALT_HEX_CODES.contains(hexCode)) {
                return false;
            }
        }

        return true;
    }

    private static void translateAltHexColor(String string, int beginIndex, StringBuilder output) {
        for (int i = 0; i < ALT_HEX_COLOR_LENGTH; i++) {
            char hexCode = string.charAt(beginIndex + i);
            output.append(ChatColor.COLOR_CHAR);
            output.append(Character.toLowerCase(hexCode));
        }
    }

    /**
     * Replaces standard chat color codes with alternate color codes, the opposite of {@link #colorize(String)}.
     */
    public static String uncolorize(@Nullable String string) {
        if (Strings.isEmpty(string) || string.indexOf(ChatColor.COLOR_CHAR) < 0) {
            return string;
        }

        StringBuilder result = new StringBuilder(string.length());

        int i = 0;
        while (i < string.length()) {
            char currentChar = string.charAt(i);

            if (currentChar == ChatColor.COLOR_CHAR && i + 1 < string.length()) {
                char nextChar = string.charAt(i + 1);

                if (nextChar == 'x' && FeatureSupport.HEX_CHAT_COLORS && isStandardHexColor(string, i + 2)) {
                    result.append(ALT_COLOR_CHAR);
                    result.append('#');
                    untranslateStandardHexColor(string, i + 2, result);

                    i += 2 + STANDARD_HEX_COLOR_LENGTH; // Skip prefix and hex string
                    continue;
                }

                if (STANDARD_COLOR_CODES.contains(nextChar)) {
                    result.append(ALT_COLOR_CHAR);
                    result.append(nextChar);

                    i += 2; // Skip color char and color code
                    continue;
                }
            }

            // Normal char
            result.append(currentChar);
            i++;
        }

        return result.toString();
    }

    private static boolean isStandardHexColor(String string, int startIndex) {
        if (string.length() - startIndex < STANDARD_HEX_COLOR_LENGTH) {
            return false;
        }

        for (int i = 0; i < STANDARD_HEX_COLOR_LENGTH; i += 2) {
            char colorChar = string.charAt(startIndex + i);
            char hexCode = string.charAt(startIndex + i + 1);

            if (colorChar != ChatColor.COLOR_CHAR || !STANDARD_HEX_CODES.contains(hexCode)) {
                return false;
            }
        }

        return true;
    }

    private static void untranslateStandardHexColor(String string, int beginIndex, StringBuilder output) {
        for (int i = 0; i < STANDARD_HEX_COLOR_LENGTH; i += 2) {
            output.append(string.charAt(beginIndex + i + 1)); // Ignore color char, only get hex codes
        }
    }

    /**
     * Color-aware equivalent of {@link String#trim()}.
     * <p>
     * Removes leading and trailing transparent whitespace, ignoring colors and formats.
     * Does not remove whitespace made visible by {@link ChatColor#STRIKETHROUGH} and {@link ChatColor#UNDERLINE}.
     */
    public static String trimTransparentWhitespace(@Nullable String string) {
        if (Strings.isEmpty(string)) {
            return string;
        }

        int firstVisibleChar = -1;
        int lastVisibleChar = -1;

        int length = string.length();
        boolean whitespaceVisible = false;

        for (int i = 0; i < length; i++) {
            char currentChar = string.charAt(i);

            if (currentChar == ' ' && !whitespaceVisible) {
                // Not visible, space if it's fully transparent

            } else if (currentChar == ChatColor.COLOR_CHAR) {
                // Not visible, color char is not rendered

                if (i < length - 1) {
                    ChatColor chatColor = ChatColor.getByChar(string.charAt(i + 1));

                    if (chatColor != null) {
                        if (chatColor == ChatColor.STRIKETHROUGH || chatColor == ChatColor.UNDERLINE) {
                            // The above formats make whitespace visible
                            whitespaceVisible = true;
                        } else if (chatColor == ChatColor.RESET || chatColor.isColor()) {
                            // Colors and "reset" make whitespace transparent again
                            whitespaceVisible = false;
                        }
                    }

                    // Skip the next character because color char prevents if from rendering, even if it's not a valid color code
                    i++;
                }

            } else {
                // Visible character

                if (firstVisibleChar == -1) {
                    // Save the position of the first visible character once
                    firstVisibleChar = i;
                }
                lastVisibleChar = i + 1; // +1 because substring() ending index is not inclusive
            }
        }

        if (firstVisibleChar == -1) {
            // The whole string is whitespace, only keep colors and formats
            return string.replace(" ", "");
        }

        // Add the visible central part of the string, as is
        String result = string.substring(firstVisibleChar, lastVisibleChar);

        if (firstVisibleChar > 0) {
            // Add the transparent left part of the string, stripping whitespace
            String leftPart = string.substring(0, firstVisibleChar).replace(" ", "");
            if (!leftPart.isEmpty()) {
                result = leftPart + result;
            }
        }

        if (lastVisibleChar < string.length()) {
            // Add the transparent right part of the string, stripping whitespace
            String rightPart = string.substring(lastVisibleChar).replace(" ", "");
            if (!rightPart.isEmpty()) {
                result = result + rightPart;
            }
        }

        return result;
    }

    /**
     * Removes repeated combinations of color and formats from a string without changing how the string is rendered.
     */
    public static String optimize(@Nullable String string) {
        if (Strings.isEmpty(string) || string.indexOf(ChatColor.COLOR_CHAR) < 0) {
            return string;
        }

        int length = string.length();
        StringBuilder result = new StringBuilder(length);
        StringBuilder previousColors = new StringBuilder();
        StringBuilder newColors = new StringBuilder();

        int i = 0;
        while (i < length) {
            char currentChar = string.charAt(i);

            if (currentChar == ChatColor.COLOR_CHAR && i < length - 1) {
                newColors.append(currentChar);
                newColors.append(string.charAt(i + 1));
                i += 2;
            } else {
                if (newColors.length() > 0) {
                    // Append new colors only if necessary
                    if (!contentEquals(newColors, previousColors)) {
                        result.append(newColors);
                    }

                    // Equivalent to, but more efficient:
                    // previousColors = newColors
                    // newColors = new StringBuilder()
                    previousColors.setLength(0);
                    previousColors.append(newColors);
                    newColors.setLength(0);
                }

                result.append(currentChar);
                i++;
            }
        }

        if (newColors.length() > 0) {
            result.append(newColors);
        }

        return result.toString();
    }

    private static boolean contentEquals(StringBuilder stringBuilder1, StringBuilder stringBuilder2) {
        if (stringBuilder1.length() != stringBuilder2.length()) {
            return false;
        }

        for (int i = 0; i < stringBuilder1.length(); i++) {
            if (stringBuilder1.charAt(i) != stringBuilder2.charAt(i)) {
                return false;
            }
        }

        return true;
    }


    private static class CharArray {

        private final char[] chars;

        CharArray(String chars) {
            this.chars = chars.toCharArray();
        }

        boolean contains(char c) {
            for (char element : chars) {
                if (c == element) {
                    return true;
                }
            }

            return false;
        }

    }

}
