/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

public class TypeWithoutClassException extends ReflectiveOperationException {

    public TypeWithoutClassException(String message) {
        super(message);
    }

}
