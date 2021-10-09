/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import me.filoghost.fcommons.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ValidReflectField<T> implements ReflectField<T> {

    private final Class<T> expectedClass;
    private final Class<T> boxedExpectedClass;
    private final Field field;

    protected ValidReflectField(Class<T> expectedClass, Field field) {
        Preconditions.notNull(expectedClass, "expectedClass");
        Preconditions.notNull(field, "field");
        this.expectedClass = expectedClass;
        this.boxedExpectedClass = ReflectUtils.boxPrimitiveClass(expectedClass);
        this.field = field;
    }

    @Override
    public Class<T> getExpectedClass() {
        return expectedClass;
    }

    @Override
    public Field getRawField() {
        return field;
    }

    @Override
    public @Nullable T getStatic() throws ReflectiveOperationException {
        return get(null);
    }

    @Override
    public @Nullable T get(Object instance) throws ReflectiveOperationException {
        checkInstance(instance);

        try {
            return boxedExpectedClass.cast(field.get(instance));
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    @Override
    public void setStatic(@Nullable T value) throws ReflectiveOperationException {
        set(null, value);
    }

    @Override
    public void set(Object instance, @Nullable T value) throws ReflectiveOperationException {
        checkInstance(instance);

        try {
            field.set(instance, boxedExpectedClass.cast(value));
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    private void checkInstance(Object instance) throws InvalidInstanceException {
        if (!Modifier.isStatic(getModifiers()) && instance == null) {
            throw new InvalidInstanceException("instance cannot be null when field is not static");
        }
    }

    @Override
    public int getModifiers() {
        return field.getModifiers();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return field.isAnnotationPresent(annotationClass);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return field.getAnnotationsByType(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return field.getAnnotations();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

}
