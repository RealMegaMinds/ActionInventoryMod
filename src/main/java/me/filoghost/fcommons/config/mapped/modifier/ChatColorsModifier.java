/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.modifier;

import me.filoghost.fcommons.Colors;

public class ChatColorsModifier implements FieldValueModifier<String, ChatColors> {

    @Override
    public String transform(ChatColors annotation, String fieldValue) {
        return Colors.colorize(fieldValue);
    }

    @Override
    public Class<ChatColors> getAnnotationType() {
        return ChatColors.class;
    }

    @Override
    public Class<String> getFieldType() {
        return String.class;
    }

}
