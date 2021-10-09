/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.types;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringConfigType extends ConfigType<String> {

    public StringConfigType(String name) {
        super(name, ConfigErrors.valueNotString);
    }

    @Override
    protected boolean isConvertibleRawValue(@Nullable Object rawValue) {
        return rawValue instanceof String || rawValue instanceof Number || rawValue instanceof Boolean || rawValue instanceof Character;
    }

    @Override
    protected @NotNull String fromRawValue(@NotNull Object rawValue) {
        return rawValue.toString();
    }

    @Override
    protected @NotNull Object toRawValue(@NotNull String configValue) {
        return configValue;
    }

}
