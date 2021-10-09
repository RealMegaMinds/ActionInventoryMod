/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.Strings;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigSyntaxException;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class YamlSerializer {

    private static final String COMMENT_PREFIX = "#";
    private static final String BLANK_CONFIG = "{}\n";

    private final Yaml yaml;

    YamlSerializer() {
        DumperOptions yamlOptions = new DumperOptions();
        yamlOptions.setIndent(2);
        yamlOptions.setWidth(Integer.MAX_VALUE); // Avoid lines wrapping
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(yamlOptions);
    }

    public LinkedHashMap<String, Object> parseRawValues(List<String> fileContents) throws ConfigLoadException {
        Preconditions.notNull(fileContents, "fileContents cannot be null");

        Map<?, ?> yamlValues = parseYamlMap(String.join("\n", fileContents));
        if (yamlValues != null) {
            return yamlMapToRawValues(yamlValues);
        } else {
            return null;
        }
    }

    public String serializeConfigValues(LinkedHashMap<String, Object> rawValues) {
        Map<String, Object> yamlMap = rawValuesToYamlMap(rawValues);
        return serializeYamlMap(yamlMap);
    }

    public List<String> parseHeader(List<String> fileContents) {
        List<String> headerLines = new ArrayList<>();
        boolean foundHeader = false;

        for (String line : fileContents) {
            if (line.startsWith(COMMENT_PREFIX)) {
                foundHeader = true;

                int headerCommentStart = COMMENT_PREFIX.length();
                if (line.length() > headerCommentStart && Character.isWhitespace(line.charAt(headerCommentStart))) {
                    headerCommentStart++;
                }

                String lineContent = line.substring(headerCommentStart);
                headerLines.add(lineContent);
            } else {
                if (foundHeader || !Strings.isWhitespace(line)) {
                    break;
                }
            }
        }

        return headerLines;
    }

    public String serializeHeader(@Nullable List<String> header) {
        if (header != null && !header.isEmpty()) {
            return header.stream().map(s -> COMMENT_PREFIX + " " + s + "\n").collect(Collectors.joining()) + "\n";
        } else {
            return "";
        }
    }

    public LinkedHashMap<String, Object> yamlMapToRawValues(Map<?, ?> yamlMap) {
        LinkedHashMap<String, Object> rawValues = new LinkedHashMap<>();

        for (Map.Entry<?, ?> entry : yamlMap.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            rawValues.put(key, yamlValueToRawValue(value));
        }

        return rawValues;
    }

    private Object yamlValueToRawValue(Object yamlValue) {
        if (yamlValue instanceof Map) {
            Map<?, ?> yamlMap = (Map<?, ?>) yamlValue;
            return new ConfigSection(yamlMapToRawValues(yamlMap));

        } else if (yamlValue instanceof List) {
            List<?> yamlList = (List<?>) yamlValue;
            List<Object> rawList = new ArrayList<>();

            for (Object yamlElement : yamlList) {
                rawList.add(yamlValueToRawValue(yamlElement));
            }

            return rawList;

        } else {
            return yamlValue;
        }
    }

    public Map<String, Object> rawValuesToYamlMap(Map<String, Object> rawValues) {
        Map<String, Object> yamlMap = new LinkedHashMap<>();

        for (Map.Entry<String, ?> entry : rawValues.entrySet()) {
            String key = entry.getKey();
            Object rawValue = entry.getValue();

            yamlMap.put(key, rawValueToYamlValue(rawValue));
        }

        return yamlMap;
    }

    private Object rawValueToYamlValue(Object rawValue) {
        if (rawValue instanceof ConfigSection) {
            ConfigSection rawSection = (ConfigSection) rawValue;
            return rawValuesToYamlMap(rawSection.getRawValues());

        } else if (rawValue instanceof List) {
            List<?> rawList = (List<?>) rawValue;
            List<Object> yamlList = new ArrayList<>();

            for (Object rawElement : rawList) {
                yamlList.add(rawValueToYamlValue(rawElement));
            }

            return yamlList;

        } else {
            return rawValue;
        }
    }

    private Map<?, ?> parseYamlMap(String serializedYamlMap) throws ConfigSyntaxException {
        Object loadedObject;
        try {
            loadedObject = yaml.load(serializedYamlMap);
        } catch (YAMLException e) {
            throw new ConfigSyntaxException(ConfigErrors.invalidYamlSyntax, e);
        }

        if (loadedObject == null) {
            return null;
        } else if (loadedObject instanceof Map) {
            return (Map<?, ?>) loadedObject;
        } else {
            throw new ConfigSyntaxException(ConfigErrors.invalidYamlSyntax, "Top level is not a Map.");
        }
    }

    private String serializeYamlMap(Map<?, ?> yamlMap) {
        String serializedYamlMap = yaml.dump(yamlMap);
        if (serializedYamlMap.equals(BLANK_CONFIG)) {
            serializedYamlMap = "";
        }
        return serializedYamlMap;
    }

}
