/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.exception;

public class ConfigValidateException extends ConfigLoadException {

    public ConfigValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigValidateException(String message) {
        super(message);
    }

}
