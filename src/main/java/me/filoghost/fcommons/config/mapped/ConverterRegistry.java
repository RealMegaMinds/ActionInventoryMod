/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import com.google.common.collect.Lists;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.mapped.converter.ConfigTypeConverter;
import me.filoghost.fcommons.config.mapped.converter.Converter;
import me.filoghost.fcommons.config.mapped.converter.ListConverter;
import me.filoghost.fcommons.config.mapped.converter.MappedConfigSectionConverter;
import me.filoghost.fcommons.reflection.TypeInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConverterRegistry {

    private static final List<ConfigTypeConverter<?>> CONFIG_VALUE_TYPE_CONVERTERS = Lists.newArrayList(
            new ConfigTypeConverter<>(ConfigType.DOUBLE, Double.class, double.class),
            new ConfigTypeConverter<>(ConfigType.FLOAT, Float.class, float.class),
            new ConfigTypeConverter<>(ConfigType.LONG, Long.class, long.class),
            new ConfigTypeConverter<>(ConfigType.INTEGER, Integer.class, int.class),
            new ConfigTypeConverter<>(ConfigType.SHORT, Short.class, short.class),
            new ConfigTypeConverter<>(ConfigType.BYTE, Byte.class, byte.class),
            new ConfigTypeConverter<>(ConfigType.BOOLEAN, Boolean.class, boolean.class),

            new ConfigTypeConverter<>(ConfigType.STRING, String.class),
            new ConfigTypeConverter<>(ConfigType.SECTION, ConfigSection.class)
    );

    @SuppressWarnings("unchecked")
    public static <T> @NotNull Converter<T, ?> fromObjectType(TypeInfo<T> typeInfo) throws ConfigMappingException {
        Class<T> typeClass = typeInfo.getTypeClass();

        if (typeClass != null) {
            for (ConfigTypeConverter<?> configTypeConverter : CONFIG_VALUE_TYPE_CONVERTERS) {
                if (configTypeConverter.supports(typeClass)) {
                    return (Converter<T, ?>) configTypeConverter;
                }
            }

            if (MappedConfigSectionConverter.supports(typeClass)) {
                return (Converter<T, ?>) new MappedConfigSectionConverter<>((TypeInfo<MappedConfigSection>) typeInfo);

            } else if (ListConverter.supports(typeClass)) {
                return (Converter<T, ?>) new ListConverter<>((TypeInfo<List<Object>>) typeInfo);
            }
        }

        throw new ConfigMappingException("cannot find suitable converter for type \"" + typeInfo + "\"");
    }

}
