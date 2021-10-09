/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.exception;

import me.filoghost.fcommons.config.ConfigPath;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigValueException extends ConfigException {

    private final ConfigPath configPath;

    public ConfigValueException(ConfigPath configPath, String message) {
        super(message);
        this.configPath = configPath;
    }

    public @Nullable ConfigPath getConfigPath() {
        return configPath;
    }

}
