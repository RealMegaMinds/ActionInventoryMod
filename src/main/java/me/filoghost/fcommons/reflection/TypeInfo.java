/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import me.filoghost.fcommons.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeInfo<T> {

    private final Type type;
    private final Class<T> typeClass;
    private final Type[] typeArguments;

    private TypeInfo(Type type, Class<T> typeClass, Type[] typeArguments) {
        Preconditions.notNull(type, "type");
        this.type = type;
        this.typeClass = typeClass;
        this.typeArguments = typeArguments;
    }

    public Type getType() {
        return type;
    }

    public @Nullable Class<T> getTypeClass() {
        return typeClass;
    }

    public @Nullable Type[] getTypeArguments() {
        return typeArguments;
    }

    public ReflectField<?>[] getDeclaredFields() throws ReflectiveOperationException {
        if (typeClass == null) {
            throw new TypeWithoutClassException("cannot read fields of type without class: " + type);
        }

        Field[] declaredFields;
        try {
            declaredFields = typeClass.getDeclaredFields();
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }

        ReflectField<?>[] output = new ReflectField[declaredFields.length];
        for (int i = 0; i < declaredFields.length; i++) {
            output[i] = ReflectField.wrap(declaredFields[i]);
        }
        return output;
    }

    public T newInstance() throws ReflectiveOperationException {
        if (typeClass == null) {
            throw new TypeWithoutClassException("cannot create instance of type without class: " + type);
        }

        try {
            Constructor<T> constructor = typeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    @Override
    public String toString() {
        return type.toString();
    }

    public static TypeInfo<?> of(ReflectField<?> reflectField) throws ReflectiveOperationException {
        Preconditions.notNull(reflectField, "reflectField");
        return TypeInfo.of(reflectField.getRawField());
    }

    public static TypeInfo<?> of(Field field) throws ReflectiveOperationException {
        Preconditions.notNull(field, "field");

        Type genericType;
        try {
            genericType = field.getGenericType();
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
        return of(genericType);
    }

    public static <T> TypeInfo<T> of(Class<T> typeClass) {
        return new TypeInfo<>(typeClass, typeClass, null);
    }

    public static TypeInfo<?> of(Type type) throws ReflectiveOperationException {
        Type[] typeArguments;
        try {
            if (type instanceof ParameterizedType) {
                typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            } else {
                typeArguments = null;
            }
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
        return new TypeInfo<>(type, ReflectUtils.getClassFromType(type), typeArguments);
    }

}
