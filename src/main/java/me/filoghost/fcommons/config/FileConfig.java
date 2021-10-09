/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import java.nio.file.Path;

public class FileConfig extends Config {

    private final Path sourceFile;

    public FileConfig(Path sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Path getSourceFile() {
        return sourceFile;
    }

}
