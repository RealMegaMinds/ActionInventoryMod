/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.types;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SectionConfigType extends ConfigType<ConfigSection> {

    public SectionConfigType(String name) {
        super(name, ConfigErrors.valueNotSection);
    }

    @Override
    protected boolean isConvertibleRawValue(@Nullable Object rawValue) {
        return rawValue instanceof ConfigSection;
    }

    @Override
    protected @NotNull ConfigSection fromRawValue(@NotNull Object rawValue) {
        return (ConfigSection) rawValue;
    }

    @Override
    protected @NotNull Object toRawValue(@NotNull ConfigSection configValue) {
        return configValue;
    }

}
