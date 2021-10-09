/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

public interface CaseInsensitiveSet extends Set<CaseInsensitiveString> {

    default boolean add(@NotNull String element) {
        return this.add(new CaseInsensitiveString(element));
    }

    default boolean remove(@NotNull String element) {
        return this.remove(new CaseInsensitiveString(element));
    }

    default boolean contains(@NotNull String element) {
        return this.contains(new CaseInsensitiveString(element));
    }

    default void addAll(@NotNull String... elements) {
        this.addAllString(Arrays.asList(elements));
    }

    default void addAllString(@NotNull Collection<? extends String> collection) {
        Collection<CaseInsensitiveString> caseInsensitiveStrings = new LinkedList<>(); // Preserve iteration order
        for (String element : collection) {
            caseInsensitiveStrings.add(new CaseInsensitiveString(element));
        }
        this.addAll(caseInsensitiveStrings); // Preserve atomicity
    }

}
