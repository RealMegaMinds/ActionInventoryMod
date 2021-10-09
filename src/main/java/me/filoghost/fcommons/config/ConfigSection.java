/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigSection extends BaseConfigSection {

    public ConfigSection() {
        super(new LinkedHashMap<>());
    }

    protected ConfigSection(LinkedHashMap<String, Object> rawValues) {
        super(rawValues);
    }

    public ConfigValue get(String path) {
        return get(ConfigPath.dotDelimited(path));
    }

    public <T> T get(String path, @NotNull ConfigType<T> configType) {
        return get(ConfigPath.dotDelimited(path), configType);
    }

    public <T> T getOrDefault(String path, @NotNull ConfigType<T> configType, @Nullable T defaultValue) {
        return getOrDefault(ConfigPath.dotDelimited(path), configType, defaultValue);
    }

    public <T> T getRequired(String path, @NotNull ConfigType<T> configType)
            throws MissingConfigValueException, InvalidConfigValueException {
        return getRequired(ConfigPath.dotDelimited(path), configType);
    }

    public void set(String path, @NotNull ConfigValue configValue) {
        set(ConfigPath.dotDelimited(path), configValue);
    }

    public <T> void set(String path, @NotNull ConfigType<T> configType, @Nullable T value) {
        set(ConfigPath.dotDelimited(path), configType, value);
    }

    public boolean contains(String path) {
        return contains(ConfigPath.dotDelimited(path));
    }

    public void remove(String path) {
        remove(ConfigPath.dotDelimited(path));
    }

    public Map<ConfigPath, ConfigValue> toMap() {
        Map<ConfigPath, ConfigValue> map = new LinkedHashMap<>();

        for (Entry<String, Object> entry : getRawValues().entrySet()) {
            ConfigPath path = ConfigPath.literal(entry.getKey());
            ConfigValue value = ConfigValue.wrapRawValue(path, entry.getValue());
            map.put(path, value);
        }

        return map;
    }

    public <T> Map<ConfigPath, T> toMap(ConfigType<T> valueTypeFilter) {
        Map<ConfigPath, T> map = new LinkedHashMap<>();

        for (Entry<String, Object> entry : getRawValues().entrySet()) {
            ConfigPath path = ConfigPath.literal(entry.getKey());
            T value = valueTypeFilter.fromRawValueOrNull(entry.getValue());
            if (value != null) {
                map.put(path, value);
            }
        }

        return map;
    }

    public ConfigSection getOrCreateSection(String path) {
        ConfigPath configPath = ConfigPath.dotDelimited(path);
        ConfigSection section = getConfigSection(configPath);
        if (section == null) {
            section = new ConfigSection();
            setConfigSection(configPath, section);
        }
        return section;
    }

    /*
     * Getters and setters with specialized type: String
     */

    public String getString(ConfigPath path) {
        return getOrDefault(path, ConfigType.STRING, null);
    }

    public String getString(String path) {
        return getString(ConfigPath.dotDelimited(path));
    }

    public String getString(ConfigPath path, String defaultValue) {
        return getOrDefault(path, ConfigType.STRING, defaultValue);
    }

    public String getString(String path, String defaultValue) {
        return getString(ConfigPath.dotDelimited(path), defaultValue);
    }

    public String getRequiredString(ConfigPath path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequired(path, ConfigType.STRING);
    }

    public String getRequiredString(String path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequiredString(ConfigPath.dotDelimited(path));
    }

    public void setString(ConfigPath path, String value) {
        set(path, ConfigType.STRING, value);
    }

    public void setString(String path, String value) {
        setString(ConfigPath.dotDelimited(path), value);
    }

    /*
     * Getters and setters with specialized type: boolean
     */

    public boolean getBoolean(ConfigPath path) {
        return getOrDefault(path, ConfigType.BOOLEAN, false);
    }

    public boolean getBoolean(String path) {
        return getBoolean(ConfigPath.dotDelimited(path));
    }

    public boolean getBoolean(ConfigPath path, boolean defaultValue) {
        return getOrDefault(path, ConfigType.BOOLEAN, defaultValue);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return getBoolean(ConfigPath.dotDelimited(path), defaultValue);
    }

    public boolean getRequiredBoolean(ConfigPath path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequired(path, ConfigType.BOOLEAN);
    }

    public boolean getRequiredBoolean(String path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequiredBoolean(ConfigPath.dotDelimited(path));
    }

    public void setBoolean(ConfigPath path, boolean value) {
        set(path, ConfigType.BOOLEAN, value);
    }

    public void setBoolean(String path, boolean value) {
        setBoolean(ConfigPath.dotDelimited(path), value);
    }

    /*
     * Getters and setters with specialized type: int
     */

    public int getInt(ConfigPath path) {
        return getOrDefault(path, ConfigType.INTEGER, 0);
    }

    public int getInt(String path) {
        return getInt(ConfigPath.dotDelimited(path));
    }

    public int getInt(ConfigPath path, int defaultValue) {
        return getOrDefault(path, ConfigType.INTEGER, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        return getInt(ConfigPath.dotDelimited(path), defaultValue);
    }

    public int getRequiredInt(ConfigPath path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequired(path, ConfigType.INTEGER);
    }

    public int getRequiredInt(String path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequiredInt(ConfigPath.dotDelimited(path));
    }

    public void setInt(ConfigPath path, int value) {
        set(path, ConfigType.INTEGER, value);
    }

    public void setInt(String path, int value) {
        setInt(ConfigPath.dotDelimited(path), value);
    }

    /*
     * Getters and setters with specialized type: double
     */

    public double getDouble(ConfigPath path) {
        return getOrDefault(path, ConfigType.DOUBLE, 0.0);
    }

    public double getDouble(String path) {
        return getDouble(ConfigPath.dotDelimited(path));
    }

    public double getDouble(ConfigPath path, double defaultValue) {
        return getOrDefault(path, ConfigType.DOUBLE, defaultValue);
    }

    public double getDouble(String path, double defaultValue) {
        return getDouble(ConfigPath.dotDelimited(path), defaultValue);
    }

    public double getRequiredDouble(ConfigPath path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequired(path, ConfigType.DOUBLE);
    }

    public double getRequiredDouble(String path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequiredDouble(ConfigPath.dotDelimited(path));
    }

    public void setDouble(ConfigPath path, double value) {
        set(path, ConfigType.DOUBLE, value);
    }

    public void setDouble(String path, double value) {
        setDouble(ConfigPath.dotDelimited(path), value);
    }

    /*
     * Getters and setters with specialized type: ConfigSection
     */

    public ConfigSection getConfigSection(ConfigPath path) {
        return getOrDefault(path, ConfigType.SECTION, null);
    }

    public ConfigSection getConfigSection(String path) {
        return getConfigSection(ConfigPath.dotDelimited(path));
    }

    public ConfigSection getConfigSection(ConfigPath path, ConfigSection defaultValue) {
        return getOrDefault(path, ConfigType.SECTION, defaultValue);
    }

    public ConfigSection getConfigSection(String path, ConfigSection defaultValue) {
        return getConfigSection(ConfigPath.dotDelimited(path), defaultValue);
    }

    public ConfigSection getRequiredConfigSection(ConfigPath path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequired(path, ConfigType.SECTION);
    }

    public ConfigSection getRequiredConfigSection(String path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequiredConfigSection(ConfigPath.dotDelimited(path));
    }

    public void setConfigSection(ConfigPath path, ConfigSection value) {
        set(path, ConfigType.SECTION, value);
    }

    public void setConfigSection(String path, ConfigSection value) {
        setConfigSection(ConfigPath.dotDelimited(path), value);
    }

    /*
     * Getters and setters with specialized type: List<String>
     */

    public List<String> getStringList(ConfigPath path) {
        return getOrDefault(path, ConfigType.STRING_LIST, null);
    }

    public List<String> getStringList(String path) {
        return getStringList(ConfigPath.dotDelimited(path));
    }

    public List<String> getStringList(ConfigPath path, List<String> defaultValue) {
        return getOrDefault(path, ConfigType.STRING_LIST, defaultValue);
    }

    public List<String> getStringList(String path, List<String> defaultValue) {
        return getStringList(ConfigPath.dotDelimited(path), defaultValue);
    }

    public List<String> getRequiredStringList(ConfigPath path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequired(path, ConfigType.STRING_LIST);
    }

    public List<String> getRequiredStringList(String path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequiredStringList(ConfigPath.dotDelimited(path));
    }

    public void setStringList(ConfigPath path, List<String> value) {
        set(path, ConfigType.STRING_LIST, value);
    }

    public void setStringList(String path, List<String> value) {
        setStringList(ConfigPath.dotDelimited(path), value);
    }

    /*
     * Getters and setters with specialized type: List<ConfigSection>
     */

    public List<ConfigSection> getSectionList(ConfigPath path) {
        return getOrDefault(path, ConfigType.SECTION_LIST, null);
    }

    public List<ConfigSection> getSectionList(String path) {
        return getSectionList(ConfigPath.dotDelimited(path));
    }

    public List<ConfigSection> getSectionList(ConfigPath path, List<ConfigSection> defaultValue) {
        return getOrDefault(path, ConfigType.SECTION_LIST, defaultValue);
    }

    public List<ConfigSection> getSectionList(String path, List<ConfigSection> defaultValue) {
        return getSectionList(ConfigPath.dotDelimited(path), defaultValue);
    }

    public List<ConfigSection> getRequiredSectionList(ConfigPath path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequired(path, ConfigType.SECTION_LIST);
    }

    public List<ConfigSection> getRequiredSectionList(String path) throws MissingConfigValueException, InvalidConfigValueException {
        return getRequiredSectionList(ConfigPath.dotDelimited(path));
    }

    public void setSectionList(ConfigPath path, List<ConfigSection> value) {
        set(path, ConfigType.SECTION_LIST, value);
    }

    public void setSectionList(String path, List<ConfigSection> value) {
        setSectionList(ConfigPath.dotDelimited(path), value);
    }

}
