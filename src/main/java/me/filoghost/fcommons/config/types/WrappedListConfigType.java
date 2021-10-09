/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.types;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigType;
import me.filoghost.fcommons.config.ConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WrappedListConfigType extends ConfigType<List<ConfigValue>> {

    public WrappedListConfigType(String name) {
        super(name, ConfigErrors.valueNotList);
    }

    @Override
    protected boolean isConvertibleRawValue(@Nullable Object rawValue) {
        return rawValue instanceof List;
    }

    @Override
    protected @NotNull List<ConfigValue> fromRawValue(@NotNull Object rawValue) {
        List<ConfigValue> configValue = new ArrayList<>();

        for (Object element : (List<?>) rawValue) {
            configValue.add(wrapRawValue(element));
        }

        return configValue;
    }

    @Override
    protected @NotNull Object toRawValue(@NotNull List<ConfigValue> configValue) {
        List<Object> rawValue = new ArrayList<>();

        for (ConfigValue element : configValue) {
            rawValue.add(getRawValue(element));
        }

        return rawValue;
    }

}
