/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigSaveException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConfigLoader {

    private final Path rootDataFolder;
    private final Path file;

    public ConfigLoader(Path rootDataFolder, Path file) {
        Preconditions.checkArgument(file.startsWith(rootDataFolder), "file \"" + file + "\""
                + " must be inside \"" + rootDataFolder + "\"");

        this.rootDataFolder = rootDataFolder;
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    public boolean fileExists() {
        return Files.isRegularFile(file);
    }

    public FileConfig init() throws ConfigSaveException, ConfigLoadException {
        createDefault();
        return load();
    }

    public FileConfig load() throws ConfigLoadException {
        Preconditions.checkState(fileExists(), "\"" + file + "\" doesn't exist or is not a regular file");

        FileConfig config = new FileConfig(file);

        try {
            config.loadFromString(Files.readAllLines(file));
        } catch (IOException e) {
            throw new ConfigLoadException(ConfigErrors.readIOException, e);
        }

        return config;
    }

    public void save(Config config) throws ConfigSaveException {
        createParentDirectory();

        String data = config.saveToString();

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write(data);
        } catch (IOException e) {
            throw new ConfigSaveException(ConfigErrors.writeDataIOException, e);
        }
    }

    public void createDefault() throws ConfigSaveException {
        if (fileExists()) {
            return;
        }

        createParentDirectory();

        Path relativeConfigPath = rootDataFolder.relativize(file);
        String internalJarPath = toInternalJarPath(relativeConfigPath);

        try (InputStream defaultFile = getInternalResource(internalJarPath)) {
            if (defaultFile != null) {
                Files.copy(defaultFile, file);
            } else {
                Files.createFile(file);
            }
        } catch (IOException e) {
            throw new ConfigSaveException(ConfigErrors.createDefaultIOException, e);
        }
    }

    private void createParentDirectory() throws ConfigSaveException {
        if (file.getParent() != null) {
            try {
                Files.createDirectories(file.getParent());
            } catch (IOException e) {
                throw new ConfigSaveException(ConfigErrors.createParentFolderIOException(rootDataFolder, file.getParent()), e);
            }
        }
    }

    private String toInternalJarPath(Path path) {
        return StreamSupport.stream(path.spliterator(), false)
                .map(Path::toString)
                .collect(Collectors.joining("/", "/", ""));
    }


    private InputStream getInternalResource(String internalJarPath) throws IOException {
        Preconditions.notNull(internalJarPath, "internalJarPath");

        URL resourceURL = getClass().getResource(internalJarPath);
        if (resourceURL == null) {
            return null;
        }

        URLConnection connection = resourceURL.openConnection();
        connection.setUseCaches(false);
        return connection.getInputStream();
    }

}
