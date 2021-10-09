/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.exception;

public class ConfigMappingException extends ConfigException {

    public ConfigMappingException(String message) {
        super(message);
    }

    public ConfigMappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
