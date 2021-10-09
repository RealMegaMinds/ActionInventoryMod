/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import me.filoghost.fcommons.Preconditions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ValidReflectMethod<T> implements ReflectMethod<T> {

    private final Class<T> boxedExpectedReturnClass;
    private final Method method;

    protected ValidReflectMethod(Class<T> expectedReturnClass, Method method) {
        Preconditions.notNull(expectedReturnClass, "expectedReturnClass");
        Preconditions.notNull(method, "method");
        this.boxedExpectedReturnClass = ReflectUtils.boxPrimitiveClass(expectedReturnClass);
        this.method = method;
    }

    @Override
    public T invoke(Object instance, Object... args) throws ReflectiveOperationException {
        if (!Modifier.isStatic(method.getModifiers()) && instance == null) {
            throw new InvalidInstanceException("instance cannot be null when method is not static");
        }

        try {
            return boxedExpectedReturnClass.cast(method.invoke(instance, args));
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    @Override
    public T invokeStatic(Object... args) throws ReflectiveOperationException {
        return invoke(null, args);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return method.getDeclaringClass();
    }

}
