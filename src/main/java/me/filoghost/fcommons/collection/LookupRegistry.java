/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import me.filoghost.fcommons.Strings;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Function;

public class LookupRegistry<V> {

    // Characters to ignore when searching values by name
    private static final char[] KEY_IGNORE_CHARS = {'-', '_', ' '};

    private final CaseInsensitiveMap<V> valuesMap;

    public static <V> LookupRegistry<V> fromValues(V[] values, Function<V, String> keyExtractor) {
        return fromValues(Arrays.asList(values), keyExtractor);
    }

    public static <V> LookupRegistry<V> fromValues(Iterable<V> values, Function<V, String> keyExtractor) {
        LookupRegistry<V> registry = new LookupRegistry<>();
        registry.putAll(values, keyExtractor);
        return registry;
    }

    protected LookupRegistry() {
        this.valuesMap = new CaseInsensitiveHashMap<>();
    }

    public @Nullable V lookup(String key) {
        if (key == null) {
            return null;
        }
        return valuesMap.get(removeIgnoredChars(key));
    }

    protected void putAll(Iterable<V> values, Function<V, String> keyExtractor) {
        for (V value : values) {
            put(keyExtractor.apply(value), value);
        }
    }

    public void put(String key, V value) {
        valuesMap.put(removeIgnoredChars(key), value);
    }

    private String removeIgnoredChars(String valueName) {
        return Strings.stripChars(valueName, KEY_IGNORE_CHARS);
    }

    @Override
    public String toString() {
        return "LookupRegistry [values=" + valuesMap + "]";
    }

}
