/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

public class InvalidInstanceException extends ReflectiveOperationException {

    public InvalidInstanceException(String message) {
        super(message);
    }

}
