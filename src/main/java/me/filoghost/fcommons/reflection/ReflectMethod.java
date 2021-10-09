/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import java.lang.reflect.Method;

public interface ReflectMethod<T> extends ReflectElement {

    static <T> ReflectMethod<T> lookup(ClassToken<T> expectedReturnClassToken,
                                       Class<?> declaringClass,
                                       String name,
                                       Class<?>... parameterTypes) {
        return lookup(expectedReturnClassToken.asClass(), declaringClass, name, parameterTypes);
    }

    static <T> ReflectMethod<T> lookup(Class<T> expectedReturnClass,
                                       Class<?> declaringClass,
                                       String name,
                                       Class<?>... parameterTypes) {
        Method method;
        try {
            method = declaringClass.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
        } catch (Throwable t) {
            return new InvalidReflectMethod<>(declaringClass, name, t);
        }
        return new ValidReflectMethod<>(expectedReturnClass, method);
    }

    T invoke(Object instance, Object... args) throws ReflectiveOperationException;

    T invokeStatic(Object... args) throws ReflectiveOperationException;

}
