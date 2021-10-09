/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class CaseInsensitiveHashSet extends HashSet<CaseInsensitiveString> implements CaseInsensitiveSet {

    public CaseInsensitiveHashSet() {}

    public CaseInsensitiveHashSet(int initialCapacity) {
        super(initialCapacity);
    }

    public CaseInsensitiveHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveHashSet(@NotNull Collection<? extends CaseInsensitiveString> collection) {
        super(collection);
    }

}
