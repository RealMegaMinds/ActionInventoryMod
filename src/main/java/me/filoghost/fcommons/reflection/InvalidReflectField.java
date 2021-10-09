/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Returned when the internal initialization of a ReflectField throws an exception.
 * Always throws the initial exception on some method calls.
 */
public class InvalidReflectField<T> implements ReflectField<T> {

    private static final Object EMPTY_ARRAY = new Object[0];

    private final Class<T> expectedClass;
    private final Class<?> declaringClass;
    private final String fieldName;
    private final ReflectiveOperationException error;

    protected InvalidReflectField(Class<T> expectedClass, Class<?> declaringClass, String fieldName, Throwable error) {
        this.expectedClass = expectedClass;
        this.declaringClass = declaringClass;
        this.fieldName = fieldName;
        if (error instanceof ReflectiveOperationException) {
            this.error = (ReflectiveOperationException) error;
        } else {
            this.error = new ReflectiveOperationException(error);
        }
    }

    @Override
    public Class<T> getExpectedClass() {
        return expectedClass;
    }

    @Override
    public Field getRawField() throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public @Nullable T get(Object instance) throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public @Nullable T getStatic() throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public void set(Object instance, @Nullable T value) throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public void setStatic(@Nullable T value) throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public int getModifiers() {
        return 0;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return false;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return (A[]) EMPTY_ARRAY;
    }

    @Override
    public Annotation[] getAnnotations() {
        return (Annotation[]) EMPTY_ARRAY;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

}
