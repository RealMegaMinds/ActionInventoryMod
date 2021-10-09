/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class CaseInsensitiveLinkedHashMap<V> extends LinkedHashMap<CaseInsensitiveString, V> implements CaseInsensitiveMap<V> {

    public CaseInsensitiveLinkedHashMap() {}

    public CaseInsensitiveLinkedHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public CaseInsensitiveLinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveLinkedHashMap(@NotNull Map<? extends CaseInsensitiveString, ? extends V> map) {
        super(map);
    }

}
