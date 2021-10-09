/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CaseInsensitiveConcurrentHashMap<V> extends ConcurrentHashMap<CaseInsensitiveString, V> implements CaseInsensitiveMap<V> {

    public CaseInsensitiveConcurrentHashMap() {}

    public CaseInsensitiveConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public CaseInsensitiveConcurrentHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    public CaseInsensitiveConcurrentHashMap(@NotNull Map<? extends CaseInsensitiveString, ? extends V> map) {
        super(map);
    }

}
