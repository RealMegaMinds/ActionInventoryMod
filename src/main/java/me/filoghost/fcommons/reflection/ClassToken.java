/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import me.filoghost.fcommons.Preconditions;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Used to capture a parameterized class. Example:
 * {@code new ClassToken<List<String>>(){}}
 */
public abstract class ClassToken<T> {

    private final Class<T> typeClass;

    @SuppressWarnings("unchecked")
    protected ClassToken() {
        Type superClass = getClass().getGenericSuperclass();
        Preconditions.checkArgument(superClass instanceof ParameterizedType, superClass + " isn't parameterized");
        Type runtimeType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        Class<?> typeClass = ReflectUtils.getClassFromType(runtimeType);
        Preconditions.checkArgument(typeClass != null, runtimeType + " doesn't declare a class");
        this.typeClass = (Class<T>) typeClass;
    }

    public final Class<T> asClass() {
        return typeClass;
    }

}
