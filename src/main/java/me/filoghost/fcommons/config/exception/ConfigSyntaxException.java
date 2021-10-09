/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.exception;

import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;

public class ConfigSyntaxException extends ConfigLoadException {

    private final String syntaxErrorDetails;

    public ConfigSyntaxException(String message, YAMLException cause) {
        super(message, cause);
        this.syntaxErrorDetails = cause instanceof MarkedYAMLException ? cause.toString() : "<could not find syntax error details>";
    }

    public ConfigSyntaxException(String message, String syntaxErrorDetails) {
        super(message);
        this.syntaxErrorDetails = syntaxErrorDetails;
    }

    public String getSyntaxErrorDetails() {
        return syntaxErrorDetails;
    }

}
