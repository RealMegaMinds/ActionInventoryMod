/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

/**
 * Returned when the internal initialization of a ReflectMethod throws an exception.
 * Always throws the initial exception on some method calls.
 */
public class InvalidReflectMethod<T> implements ReflectMethod<T> {

    private final Class<?> declaringClass;
    private final String methodName;
    private final ReflectiveOperationException error;

    protected InvalidReflectMethod(Class<?> declaringClass, String methodName, Throwable error) {
        this.declaringClass = declaringClass;
        this.methodName = methodName;
        if (error instanceof ReflectiveOperationException) {
            this.error = (ReflectiveOperationException) error;
        } else {
            this.error = new ReflectiveOperationException(error);
        }
    }

    @Override
    public T invoke(Object instance, Object... args) throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public T invokeStatic(Object... args) throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String getName() {
        return methodName;
    }

    @Override
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

}
