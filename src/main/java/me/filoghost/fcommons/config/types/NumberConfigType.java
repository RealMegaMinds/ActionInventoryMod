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

import java.util.function.Function;

public class NumberConfigType<T extends Number> extends ConfigType<T> {

    private final Function<Number, T> toTypeFunction;

    public NumberConfigType(String name, Function<Number, T> toTypeFunction) {
        super(name, ConfigErrors.valueNotNumber);
        this.toTypeFunction = toTypeFunction;
    }

    @Override
    protected boolean isConvertibleRawValue(@Nullable Object rawValue) {
        return rawValue instanceof Number;
    }

    @Override
    protected @NotNull T fromRawValue(@NotNull Object rawValue) {
        return toTypeFunction.apply((Number) rawValue);
    }

    @Override
    protected @NotNull Object toRawValue(@NotNull T configValue) {
        return configValue;
    }

}
