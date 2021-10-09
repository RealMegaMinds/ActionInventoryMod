/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CaseInsensitiveHashMap<V> extends HashMap<CaseInsensitiveString, V> implements CaseInsensitiveMap<V> {

    public CaseInsensitiveHashMap() {}

    public CaseInsensitiveHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public CaseInsensitiveHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveHashMap(@NotNull Map<? extends CaseInsensitiveString, ? extends V> map) {
        super(map);
    }

}
