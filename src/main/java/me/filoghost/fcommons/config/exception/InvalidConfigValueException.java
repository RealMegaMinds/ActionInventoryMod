/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.exception;

import me.filoghost.fcommons.config.ConfigPath;

public class InvalidConfigValueException extends ConfigValueException {

    public InvalidConfigValueException(ConfigPath configPath, String message) {
        super(configPath, message);
    }

}
