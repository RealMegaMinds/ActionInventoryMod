/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

abstract class BaseConfigSection {

    private LinkedHashMap<String, Object> rawValues;

    protected BaseConfigSection(LinkedHashMap<String, Object> rawValues) {
        this.setRawValues(rawValues);
    }

    protected void setRawValues(LinkedHashMap<String, Object> rawValues) {
        Preconditions.notNull(rawValues, "rawValues");
        this.rawValues = rawValues;
    }

    protected LinkedHashMap<String, Object> getRawValues() {
        return rawValues;
    }

    public @NotNull ConfigValue get(ConfigPath path) {
        return ConfigValue.wrapRawValue(path, getRawValue(path));
    }

    public <T> @Nullable T get(ConfigPath path, @NotNull ConfigType<T> type) {
        return getOrDefault(path, type, null);
    }

    public <T> T getOrDefault(ConfigPath path, @NotNull ConfigType<T> type, @Nullable T defaultValue) {
        return type.fromRawValueOrDefault(getRawValue(path), defaultValue);
    }

    public <T> @NotNull T getRequired(ConfigPath path, @NotNull ConfigType<T> type)
            throws MissingConfigValueException, InvalidConfigValueException {
        return type.fromRawValueRequired(getRawValue(path), path);
    }

    public void set(ConfigPath path, @NotNull ConfigValue configValue) {
        setRawValue(path, configValue.getRawValue());
    }

    public <T> void set(ConfigPath path, @NotNull ConfigType<T> type, @Nullable T value) {
        Object rawValue;
        if (value != null) {
            rawValue = type.toRawValue(value);
        } else {
            rawValue = null;
        }
        setRawValue(path, rawValue);
    }

    public boolean contains(ConfigPath path) {
        return getRawValue(path) != null;
    }

    public void remove(ConfigPath path) {
        setRawValue(path, null);
    }

    private @Nullable Object getRawValue(ConfigPath path) {
        Preconditions.notNull(path, "path");

        BaseConfigSection targetSection = getParentSectionForPath(path);
        if (targetSection == null) {
            return null;
        }

        return targetSection.rawValues.get(path.getLastPart());
    }

    private void setRawValue(ConfigPath path, @Nullable Object value) {
        Preconditions.notNull(path, "path");

        if (value != null) {
            getOrCreateParentSectionForPath(path).rawValues.put(path.getLastPart(), value);
        } else {
            BaseConfigSection section = getParentSectionForPath(path);
            if (section != null) {
                section.rawValues.remove(path.getLastPart());
            }
        }
    }

    private @Nullable BaseConfigSection getParentSectionForPath(ConfigPath path) {
        return getParentSectionForPath(path, false);
    }

    protected @NotNull BaseConfigSection getOrCreateParentSectionForPath(ConfigPath path) {
        return getParentSectionForPath(path, true);
    }

    @Contract("_, true -> !null")
    private BaseConfigSection getParentSectionForPath(ConfigPath path, boolean createIfNotExisting) {
        BaseConfigSection currentSection = this;

        // Iterate on the path parts except the last one
        for (int i = 0; i < path.getPartsLength() - 1; i++) {
            String pathPart = path.getPart(i);
            Object rawSectionValue = currentSection.rawValues.get(pathPart);

            if (createIfNotExisting && rawSectionValue == null) {
                ConfigSection innerSection = new ConfigSection();
                currentSection.rawValues.put(pathPart, ConfigType.SECTION.toRawValue(innerSection));
                currentSection = innerSection;
            } else {
                currentSection = ConfigType.SECTION.fromRawValueOrNull(rawSectionValue);
                if (currentSection == null) {
                    return null;
                }
            }
        }

        return currentSection;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BaseConfigSection)) {
            return false;
        }
        BaseConfigSection other = (BaseConfigSection) obj;
        return this.rawValues.equals(other.rawValues);
    }

    @Override
    public final int hashCode() {
        return rawValues.hashCode();
    }

    @Override
    public String toString() {
        return rawValues.toString();
    }

}
