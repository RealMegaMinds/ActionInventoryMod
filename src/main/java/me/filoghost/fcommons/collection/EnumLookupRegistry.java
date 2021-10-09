/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import java.util.Arrays;

public class EnumLookupRegistry<E extends Enum<E>> extends LookupRegistry<E> {

    private final Class<E> enumType;

    public EnumLookupRegistry(Class<E> enumType) {
        this.enumType = enumType;
    }

    public static <E extends Enum<E>> EnumLookupRegistry<E> fromEnumValues(Class<E> enumClass) {
        EnumLookupRegistry<E> registry = new EnumLookupRegistry<>(enumClass);
        registry.putAll(Arrays.asList(enumClass.getEnumConstants()), Enum::name);
        return registry;
    }

    public void putEnumIfExisting(String key, String enumName) {
        try {
            E value = Enum.valueOf(enumType, enumName);
            put(key, value);
        } catch (IllegalArgumentException e) {
            // Ignore, enum name does not exist
        }
    }

}
