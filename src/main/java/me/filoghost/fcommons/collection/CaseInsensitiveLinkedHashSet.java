/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;

public class CaseInsensitiveLinkedHashSet extends LinkedHashSet<CaseInsensitiveString> implements CaseInsensitiveSet {

    public CaseInsensitiveLinkedHashSet() {}

    public CaseInsensitiveLinkedHashSet(int initialCapacity) {
        super(initialCapacity);
    }

    public CaseInsensitiveLinkedHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveLinkedHashSet(@NotNull Collection<? extends CaseInsensitiveString> collection) {
        super(collection);
    }

}
