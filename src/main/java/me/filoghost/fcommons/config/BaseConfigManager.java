/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.config.mapped.MappedConfig;
import me.filoghost.fcommons.config.mapped.MappedConfigLoader;

import java.nio.file.Path;

public class BaseConfigManager {

    protected final Path rootDataFolder;

    public BaseConfigManager(Path rootDataFolder) {
        this.rootDataFolder = rootDataFolder;
    }

    public Path getRootDataFolder() {
        return rootDataFolder;
    }

    public ConfigLoader getConfigLoader(String fileName) {
        return getConfigLoader(rootDataFolder.resolve(fileName));
    }

    public ConfigLoader getConfigLoader(Path configPath) {
        return new ConfigLoader(rootDataFolder, configPath);
    }

    public <T extends MappedConfig> MappedConfigLoader<T> getMappedConfigLoader(String fileName, Class<T> mappedConfigClass) {
        return getMappedConfigLoader(rootDataFolder.resolve(fileName), mappedConfigClass);
    }

    public <T extends MappedConfig> MappedConfigLoader<T> getMappedConfigLoader(Path configPath, Class<T> mappedConfigClass) {
        return new MappedConfigLoader<>(rootDataFolder, configPath, mappedConfigClass);
    }

}
