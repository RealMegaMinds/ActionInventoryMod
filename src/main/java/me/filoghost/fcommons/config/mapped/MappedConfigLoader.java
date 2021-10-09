/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.Config;
import me.filoghost.fcommons.config.ConfigLoader;
import me.filoghost.fcommons.config.ConfigPath;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigSaveException;
import me.filoghost.fcommons.reflection.TypeInfo;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

public class MappedConfigLoader<T extends MappedConfig> {

    private final TypeInfo<T> mappedTypeInfo;
    private ConfigMapper<T> configMapper;
    private final ConfigLoader configLoader;

    private Map<ConfigPath, ConfigValue> defaultValues;

    public MappedConfigLoader(Path rootDataFolder, Path configPath, Class<T> mappedConfigClass) {
        this.mappedTypeInfo = TypeInfo.of(mappedConfigClass);
        this.configLoader = new ConfigLoader(rootDataFolder, configPath);
    }

    protected ConfigMapper<T> getMapper() throws ConfigMappingException {
        if (configMapper == null) {
            configMapper = new ConfigMapper<>(mappedTypeInfo);
        }
        return configMapper;
    }

    public @NotNull T load() throws ConfigLoadException {
        Config config = configLoader.load();

        try {
            T mappedObject = getMapper().newMappedObjectInstance();
            mappedObject.beforeLoad(config);
            getMapper().setFieldsFromConfig(mappedObject, config);
            return mappedObject;
        } catch (ConfigMappingException e) {
            throw new ConfigLoadException(e.getMessage(), e);
        }
    }

    public @NotNull T init() throws ConfigLoadException, ConfigSaveException {
        Config config = configLoader.init();

        try {
            T mappedObject = getMapper().newMappedObjectInstance();
            boolean modifiedBeforeLoad = mappedObject.beforeLoad(config);

            // On the first time, save defaults before modifying the config
            if (defaultValues == null) {
                defaultValues = getMapper().getFieldsAsConfigValues(mappedObject);
            }

            boolean addedNewDefaultValues = addMissingDefaultValues(config, defaultValues);

            getMapper().setFieldsFromConfig(mappedObject, config);

            if (modifiedBeforeLoad || addedNewDefaultValues) {
                saveInternal(mappedObject, config, false);
            }

            return mappedObject;

        } catch (ConfigMappingException e) {
            throw new ConfigLoadException(e.getMessage(), e);
        }
    }

    private boolean addMissingDefaultValues(ConfigSection config, Map<ConfigPath, ConfigValue> defaultValues) {
        boolean modified = false;

        for (Entry<ConfigPath, ConfigValue> entry : defaultValues.entrySet()) {
            if (!config.contains(entry.getKey())) {
                config.set(entry.getKey(), entry.getValue());
                modified = true;
            }
        }

        return modified;
    }

    public boolean saveIfDifferent(@NotNull T newMappedObject) throws ConfigLoadException, ConfigSaveException {
        if (!configLoader.fileExists()) {
            save(newMappedObject);
            return true;
        }

        Config config = configLoader.load();

        try {
            if (getMapper().equalsConfig(newMappedObject, config)) {
                return false;
            } else {
                saveInternal(newMappedObject, config, true);
                return true;
            }
        } catch (ConfigMappingException e) {
            throw new ConfigLoadException(e.getMessage(), e);
        }
    }

    public void save(@NotNull T mappedObject) throws ConfigSaveException {
        saveInternal(mappedObject, new Config(), true);
    }

    private void saveInternal(@NotNull T mappedObject, @NotNull Config config, boolean writeMappedObject) throws ConfigSaveException {
        if (writeMappedObject) {
            try {
                getMapper().setConfigFromFields(mappedObject, config);
            } catch (ConfigMappingException e) {
                throw new ConfigSaveException(e.getMessage(), e);
            }
        }

        config.setHeader(mappedObject.getHeader());
        configLoader.save(config);
    }

    public Path getFile() {
        return configLoader.getFile();
    }

}
