/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.modifier;

import java.lang.annotation.Annotation;

public interface FieldValueModifier<F, A extends Annotation> {

    F transform(A annotation, F fieldValue);

    Class<A> getAnnotationType();

    Class<F> getFieldType();

    default boolean isApplicable(Annotation annotation, Object value) {
        return getAnnotationType().isInstance(annotation) && getFieldType().isInstance(value);
    }

}
