/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.Config;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public interface MappedConfig extends MappedConfigSection {

    default @NotNull List<String> getHeader() {
        return Collections.emptyList();
    }

    /**
     * Applies changes to the raw config before loading.
     *
     * @return true if the applied changes should trigger a file save, false otherwise. This result is ignored
     *         when the configuration is only being loaded.
     */
    default boolean beforeLoad(Config rawConfig) {
        return false;
    }

}
