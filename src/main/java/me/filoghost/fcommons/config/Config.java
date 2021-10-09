/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import com.google.common.collect.ImmutableList;
import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.exception.ConfigLoadException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Config extends ConfigSection {

    private final YamlSerializer yamlSerializer;
    private List<String> header;

    public Config() {
        this.yamlSerializer = new YamlSerializer();
    }

    public List<String> getHeader() {
        return this.header;
    }

    public void setHeader(String... header) {
        setHeader(Arrays.asList(header));
    }

    public void setHeader(List<String> header) {
        this.header = ImmutableList.copyOf(header);
    }

    protected void loadFromString(List<String> fileContents) throws ConfigLoadException {
        Preconditions.notNull(fileContents, "fileContents cannot be null");

        LinkedHashMap<String, Object> rawValues = yamlSerializer.parseRawValues(fileContents);
        if (rawValues == null) {
            rawValues = new LinkedHashMap<>();
        }

        setHeader(yamlSerializer.parseHeader(fileContents));
        setRawValues(rawValues);
    }

    protected String saveToString() {
        String serializedHeader = yamlSerializer.serializeHeader(header);
        String serializedValues = yamlSerializer.serializeConfigValues(getRawValues());

        return serializedHeader + serializedValues;
    }
}
