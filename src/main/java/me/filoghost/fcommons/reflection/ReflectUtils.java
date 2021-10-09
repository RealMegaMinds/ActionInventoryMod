/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectUtils {

    public static boolean isClassLoaded(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> boxPrimitiveClass(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == double.class) {
                return (Class<T>) Double.class;
            } else if (clazz == float.class) {
                return (Class<T>) Float.class;
            } else if (clazz == long.class) {
                return (Class<T>) Long.class;
            } else if (clazz == int.class) {
                return (Class<T>) Integer.class;
            } else if (clazz == short.class) {
                return (Class<T>) Short.class;
            } else if (clazz == byte.class) {
                return (Class<T>) Byte.class;
            } else if (clazz == char.class) {
                return (Class<T>) Character.class;
            } else if (clazz == boolean.class) {
                return (Class<T>) Boolean.class;
            } else if (clazz == void.class) {
                return (Class<T>) Void.class;
            }
        }

        return clazz;
    }

    public static @Nullable Class<?> getClassFromType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() instanceof Class) {
                return (Class<?>) parameterizedType.getRawType();
            }
        }
        return null;
    }

}
