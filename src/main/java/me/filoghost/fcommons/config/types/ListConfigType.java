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

import java.util.ArrayList;
import java.util.List;

public class ListConfigType<E> extends ConfigType<List<E>> {

    private final ConfigType<E> elementType;

    public ListConfigType(String name, ConfigType<E> elementType) {
        super(name, ConfigErrors.valueNotList);
        this.elementType = elementType;
    }

    @Override
    protected boolean isConvertibleRawValue(@Nullable Object rawValue) {
        return rawValue instanceof List;
    }

    @Override
    protected @NotNull List<@NotNull E> fromRawValue(@NotNull Object rawValue) {
        List<@NotNull E> configValue = new ArrayList<>();

        for (Object rawElement : (List<?>) rawValue) {
            E configElement = fromRawValueOrNull(elementType, rawElement);
            if (configElement != null) {
                configValue.add(configElement);
            }
        }

        return configValue;
    }

    @Override
    protected @NotNull Object toRawValue(@NotNull List<@Nullable E> configValue) {
        List<Object> rawValue = new ArrayList<>();

        for (E configElement : configValue) {
            if (configElement != null) {
                rawValue.add(toRawValue(elementType, configElement));
            } else {
                rawValue.add(null);
            }
        }

        return rawValue;
    }

}
