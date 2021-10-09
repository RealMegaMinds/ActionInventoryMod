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

public class BooleanConfigType extends ConfigType<Boolean> {

    public BooleanConfigType(String name) {
        super(name, ConfigErrors.valueNotBoolean);
    }

    @Override
    protected boolean isConvertibleRawValue(@Nullable Object rawValue) {
        return rawValue instanceof Boolean;
    }

    @Override
    protected @NotNull Boolean fromRawValue(@NotNull Object rawValue) {
        return (Boolean) rawValue;
    }

    @Override
    protected @NotNull Object toRawValue(@NotNull Boolean configValue) {
        return configValue;
    }

}
