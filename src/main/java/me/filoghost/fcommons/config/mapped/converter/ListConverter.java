/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigType;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigValidateException;
import me.filoghost.fcommons.config.mapped.ConverterRegistry;
import me.filoghost.fcommons.reflection.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ListConverter<E> extends Converter<List<E>, List<ConfigValue>> {

    private final Converter<E, ?> elementConverter;

    @SuppressWarnings("unchecked")
    public ListConverter(TypeInfo<List<E>> fieldTypeInfo) throws ConfigMappingException {
        super(ConfigType.LIST);
        TypeInfo<E> elementTypeInfo = (TypeInfo<E>) ConverterHelper.getSingleGenericType(fieldTypeInfo);
        this.elementConverter = ConverterRegistry.fromObjectType(elementTypeInfo);
    }

    @Override
    protected @NotNull List<@NotNull ConfigValue> toConfigValue0(@NotNull List<@Nullable E> fieldValue) throws ConfigMappingException {
        List<@NotNull ConfigValue> configList = new ArrayList<>();

        for (E fieldElement : fieldValue) {
            if (fieldElement != null) {
                configList.add(elementConverter.toConfigValue(fieldElement));
            } else {
                configList.add(ConfigValue.NULL);
            }
        }

        return configList;
    }

    @Override
    protected @NotNull List<@Nullable E> toFieldValue0(@NotNull List<@NotNull ConfigValue> configList) throws ConfigMappingException, ConfigValidateException {
        List<@Nullable E> fieldList = new ArrayList<>();

        for (ConfigValue configElement : configList) {
            if (elementConverter.isValidConfigValue(configElement)) {
                E fieldValue = elementConverter.toFieldValue(configElement);
                fieldList.add(fieldValue);
            }
        }

        return fieldList;
    }

    @Override
    protected boolean equalsConfig0(@NotNull List<@Nullable E> fieldList, @NotNull List<@NotNull ConfigValue> configList) throws ConfigMappingException {
        // Skip elements that would be skipped during read
        List<ConfigValue> filteredConfigList = new ArrayList<>();

        for (ConfigValue configValue : configList) {
            if (elementConverter.isValidConfigValue(configValue)) {
                filteredConfigList.add(configValue);
            }
        }

        if (filteredConfigList.size() != fieldList.size()) {
            return false;
        }

        for (int i = 0; i < filteredConfigList.size(); i++) {
            ConfigValue configElement = filteredConfigList.get(i);
            E fieldElement = fieldList.get(i);

            if (!elementConverter.equalsConfig(fieldElement, configElement)) {
                return false;
            }
        }

        return true;
    }

    public static boolean supports(Class<?> typeClass) {
        return typeClass == List.class;
    }

}
