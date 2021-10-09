/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import com.google.common.collect.ImmutableList;
import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigPath;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigValidateException;
import me.filoghost.fcommons.reflection.ReflectField;
import me.filoghost.fcommons.reflection.TypeInfo;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigMapper<T extends MappedConfigSection> {

    private final TypeInfo<T> mappedTypeInfo;
    private final List<MappedField<?>> mappedFields;

    public ConfigMapper(TypeInfo<T> typeInfo) throws ConfigMappingException {
        try {
            this.mappedTypeInfo = typeInfo;
            ImmutableList.Builder<MappedField<?>> mappedFieldsBuilder = ImmutableList.builder();
            for (ReflectField<?> field : typeInfo.getDeclaredFields()) {
                if (isMappable(field)) {
                    mappedFieldsBuilder.add(new MappedField<>(field));
                }
            }
            this.mappedFields = mappedFieldsBuilder.build();
        } catch (ReflectiveOperationException e) {
            throw new ConfigMappingException(ConfigErrors.mapperReflectionException(typeInfo), e);
        }
    }

    public T newMappedObjectInstance() throws ConfigMappingException {
        try {
            return mappedTypeInfo.newInstance();
        } catch (NoSuchMethodException e) {
            throw new ConfigMappingException(ConfigErrors.noEmptyConstructor(mappedTypeInfo));
        } catch (ReflectiveOperationException e) {
            throw new ConfigMappingException(ConfigErrors.cannotCreateInstance(mappedTypeInfo), e);
        }
    }

    public Map<ConfigPath, ConfigValue> getFieldsAsConfigValues(@NotNull T mappedObject) throws ConfigMappingException {
        Map<ConfigPath, ConfigValue> configValues = new LinkedHashMap<>();

        for (MappedField<?> mappedField : mappedFields) {
            ConfigValue configValue = mappedField.readConfigValueFromObject(mappedObject);
            configValues.put(mappedField.getConfigPath(), configValue);
        }

        return configValues;
    }

    public void setConfigFromFields(@NotNull T mappedObject, @NotNull ConfigSection config) throws ConfigMappingException {
        for (MappedField<?> mappedField : mappedFields) {
            ConfigValue configValue = mappedField.readConfigValueFromObject(mappedObject);
            config.set(mappedField.getConfigPath(), configValue);
        }
    }

    public void setFieldsFromConfig(@NotNull T mappedObject, @NotNull ConfigSection config) throws ConfigMappingException, ConfigValidateException {
        for (MappedField<?> mappedField : mappedFields) {
            mappedField.setFieldValueFromConfig(mappedObject, config);
        }
        mappedObject.afterLoad();
    }

    private boolean isMappable(ReflectField<?> field) {
        int modifiers = field.getModifiers();

        return !Modifier.isStatic(modifiers)
                && !Modifier.isTransient(modifiers)
                && !Modifier.isFinal(modifiers);
    }

    public boolean equalsConfig(@NotNull T mappedObject, @NotNull ConfigSection config) throws ConfigMappingException {
        for (MappedField<?> mappedField : mappedFields) {
            if (!mappedField.equalsConfigValue(mappedObject, config)) {
                return false;
            }
        }

        return true;
    }

}
