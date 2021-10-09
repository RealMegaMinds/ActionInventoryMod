/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import me.filoghost.fcommons.config.types.BooleanConfigType;
import me.filoghost.fcommons.config.types.ListConfigType;
import me.filoghost.fcommons.config.types.NumberConfigType;
import me.filoghost.fcommons.config.types.SectionConfigType;
import me.filoghost.fcommons.config.types.StringConfigType;
import me.filoghost.fcommons.config.types.WrappedListConfigType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ConfigType<T> {

    public static final ConfigType<String> STRING = new StringConfigType("STRING");
    public static final ConfigType<Boolean> BOOLEAN = new BooleanConfigType("BOOLEAN");
    public static final ConfigType<Long> LONG = new NumberConfigType<>("LONG", Number::longValue);
    public static final ConfigType<Integer> INTEGER = new NumberConfigType<>("INTEGER", Number::intValue);
    public static final ConfigType<Short> SHORT = new NumberConfigType<>("SHORT", Number::shortValue);
    public static final ConfigType<Byte> BYTE = new NumberConfigType<>("BYTE", Number::byteValue);
    public static final ConfigType<Double> DOUBLE = new NumberConfigType<>("DOUBLE", Number::doubleValue);
    public static final ConfigType<Float> FLOAT = new NumberConfigType<>("FLOAT", Number::floatValue);

    public static final ConfigType<ConfigSection> SECTION = new SectionConfigType("SECTION");

    public static final ConfigType<List<ConfigValue>> LIST = new WrappedListConfigType("LIST");
    public static final ConfigType<List<String>> STRING_LIST = new ListConfigType<>("STRING_LIST", STRING);
    public static final ConfigType<List<Integer>> INTEGER_LIST = new ListConfigType<>("INTEGER_LIST", INTEGER);
    public static final ConfigType<List<ConfigSection>> SECTION_LIST = new ListConfigType<>("SECTION_LIST", SECTION);


    private final String name;
    private final String notConvertibleErrorMessage;

    protected ConfigType(String name, String notConvertibleErrorMessage) {
        this.name = name;
        this.notConvertibleErrorMessage = notConvertibleErrorMessage;
    }

    @Contract("null -> false")
    protected abstract boolean isConvertibleRawValue(@Nullable Object rawValue);

    protected abstract @NotNull Object toRawValue(@NotNull T configValue);

    protected abstract @NotNull T fromRawValue(@NotNull Object rawValue);

    protected @Nullable T fromRawValueOrNull(@Nullable Object rawValue) {
        return fromRawValueOrDefault(rawValue, null);
    }

    @Contract("_, !null -> !null")
    protected T fromRawValueOrDefault(@Nullable Object rawValue, T defaultValue) {
        if (isConvertibleRawValue(rawValue)) {
            return fromRawValue(rawValue);
        } else {
            return defaultValue;
        }
    }

    protected @NotNull T fromRawValueRequired(@Nullable Object rawValue, @Nullable ConfigPath configPath)
            throws InvalidConfigValueException, MissingConfigValueException {
        if (isConvertibleRawValue(rawValue)) {
            return fromRawValue(rawValue);
        } else {
            if (rawValue != null) {
                throw new InvalidConfigValueException(configPath, notConvertibleErrorMessage);
            } else {
                throw new MissingConfigValueException(configPath, ConfigErrors.valueIsMissing);
            }
        }
    }

    // Access for subclasses
    protected static ConfigValue wrapRawValue(@Nullable Object rawValue) {
        return ConfigValue.wrapRawValue(null, rawValue);
    }

    // Access for subclasses
    protected static Object getRawValue(@NotNull ConfigValue configValue) {
        return configValue.getRawValue();
    }

    // Access for subclasses
    protected static <T> Object toRawValue(@NotNull ConfigType<T> type, @NotNull T configValue) {
        return type.toRawValue(configValue);
    }

    // Access for subclasses
    protected static <T> T fromRawValueOrNull(@NotNull ConfigType<T> type, @Nullable Object rawValue) {
        return type.fromRawValueOrNull(rawValue);
    }

    @Override
    public String toString() {
        return name;
    }

}
