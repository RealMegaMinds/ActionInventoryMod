/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface ReflectField<T> extends ReflectElement {

    static <T> ReflectField<T> lookup(ClassToken<T> expectedClassToken, Class<?> declaringClass, String fieldName) {
        return lookup(expectedClassToken.asClass(), declaringClass, fieldName);
    }

    static <T> ReflectField<T> lookup(Class<T> expectedClass, Class<?> declaringClass, String fieldName) {
        Field field;
        try {
            field = declaringClass.getDeclaredField(fieldName);
        } catch (Throwable t) {
            return new InvalidReflectField<>(expectedClass, declaringClass, fieldName, t);
        }
        return wrap(expectedClass, field);
    }

    static ReflectField<?> wrap(Field field) {
        return wrap(field.getType(), field);
    }

    static <T> ReflectField<T> wrap(ClassToken<T> expectedClassToken, Field field) {
        return wrap(expectedClassToken.asClass(), field);
    }

    static <T> ReflectField<T> wrap(Class<T> expectedClass, Field field) {
        try {
            field.setAccessible(true);
        } catch (Throwable t) {
            return new InvalidReflectField<>(expectedClass, field.getDeclaringClass(), field.getName(), t);
        }
        return new ValidReflectField<>(expectedClass, field);
    }

    Class<T> getExpectedClass();

    Field getRawField() throws ReflectiveOperationException;

    T get(Object instance) throws ReflectiveOperationException;

    T getStatic() throws ReflectiveOperationException;

    void set(Object instance, T value) throws ReflectiveOperationException;

    void setStatic(T value) throws ReflectiveOperationException;

    int getModifiers();

    boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

    <A extends Annotation> A getAnnotation(Class<A> annotationClass);

    <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass);

    Annotation[] getAnnotations();

}
