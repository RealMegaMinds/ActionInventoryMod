/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class CaseInsensitiveConcurrentHashSet extends ForwardingSet<CaseInsensitiveString> implements CaseInsensitiveSet {

    public CaseInsensitiveConcurrentHashSet() {
        super(Collections.newSetFromMap(new ConcurrentHashMap<>()));
    }

    public CaseInsensitiveConcurrentHashSet(int initialCapacity) {
        super(Collections.newSetFromMap(new ConcurrentHashMap<>(initialCapacity)));
    }

    public CaseInsensitiveConcurrentHashSet(int initialCapacity, float loadFactor) {
        super(Collections.newSetFromMap(new ConcurrentHashMap<>(initialCapacity, loadFactor)));
    }

    public CaseInsensitiveConcurrentHashSet(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(Collections.newSetFromMap(new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel)));
    }

    public CaseInsensitiveConcurrentHashSet(@NotNull Collection<? extends CaseInsensitiveString> collection) {
        super(Collections.newSetFromMap(new ConcurrentHashMap<>()));
        addAll(collection); // The ConcurrentHashMap constructor that accepts another map does this internally
    }

}
