/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.config.mapped.MappedField;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.nio.file.Path;

public class ConfigErrors {

    public static final String readIOException = "I/O exception while reading file";
    public static final String createDefaultIOException = "I/O exception while creating default file";
    public static final String writeDataIOException = "I/O exception while writing data to file";
    public static final String invalidYamlSyntax = "invalid YAML syntax";

    public static final String valueIsMissing = "value is missing";
    public static final String valueNotList = "value is not a list";
    public static final String valueNotBoolean = "value is not a boolean";
    public static final String valueNotNumber = "value is not a number";
    public static final String valueNotString = "value is not a string";
    public static final String valueNotSection = "value is not a configuration section";

    public static String createParentFolderIOException(Path rootDataFolder, Path folder) {
        return "I/O exception while creating parent directory \"" + formatPath(rootDataFolder, folder) + "\"";
    }

    public static String mapperReflectionException(TypeInfo<?> typeInfo) {
        return "reflection error on mapped type \"" + typeInfo + "\"";
    }

    public static String noEmptyConstructor(TypeInfo<?> typeInfo) {
        return "mapped type \"" + typeInfo + "\" has no constructor without parameters";
    }

    public static String cannotCreateInstance(TypeInfo<?> typeInfo) {
        return "couldn't create new instance of mapped type \"" + typeInfo + "\"";
    }

    public static String fieldReadError(MappedField<?> mappedField) {
        return "error while reading field \"" + mappedField.getFieldName() + "\""
                + " in class \"" + mappedField.getDeclaringClass() + "\"";
    }

    public static String fieldWriteError(MappedField<?> mappedField) {
        return "error while writing field \"" + mappedField.getFieldName() + "\""
                + " in class \"" + mappedField.getDeclaringClass() + "\"";
    }

    public static String conversionFailed(MappedField<?> mappedField) {
        return "error while converting field \"" + mappedField.getFieldName() + "\""
                + " in class \"" + mappedField.getDeclaringClass() + "\"";
    }

    public static String formatPath(Path rootDataFolder, Path path) {
        if (path.startsWith(rootDataFolder)) {
            // Remove root data folder prefix
            return path.subpath(rootDataFolder.getNameCount(), path.getNameCount()).toString();
        } else {
            return path.toString();
        }
    }

}
