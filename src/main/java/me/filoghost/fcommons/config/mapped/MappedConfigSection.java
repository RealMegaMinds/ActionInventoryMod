/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.exception.ConfigValidateException;

public interface MappedConfigSection {

    /**
     * Optional hook called after fields are injected, can be used to set additional fields and
     * to validate the configuration.
     */
    default void afterLoad() throws ConfigValidateException {}

}
