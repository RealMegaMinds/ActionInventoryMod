/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

public interface ReflectElement {

    boolean isValid();

    String getName();

    Class<?> getDeclaringClass();

}
