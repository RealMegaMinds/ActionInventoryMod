/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface CaseInsensitiveMap<V> extends Map<CaseInsensitiveString, V> {

    default V put(@NotNull String key, V value) {
        return put(new CaseInsensitiveString(key), value);
    }

    default V remove(@NotNull String key) {
        return remove(new CaseInsensitiveString(key));
    }

    default V get(@NotNull String key) {
        return get(new CaseInsensitiveString(key));
    }

    default V getOrDefault(@NotNull String key, V defaultValue) {
        return getOrDefault(new CaseInsensitiveString(key), defaultValue);
    }

    default boolean containsKey(@NotNull String key) {
        return containsKey(new CaseInsensitiveString(key));
    }

    default boolean remove(@NotNull String key, V value) {
        return remove(new CaseInsensitiveString(key), value);
    }

    default void putAllString(@NotNull Map<? extends String, ? extends V> map) {
        Map<CaseInsensitiveString, V> caseInsensitiveStringMap = new LinkedHashMap<>(); // Preserve iteration order
        for (Entry<? extends String, ? extends V> entry : map.entrySet()) {
            caseInsensitiveStringMap.put(new CaseInsensitiveString(entry.getKey()), entry.getValue());
        }
        putAll(caseInsensitiveStringMap); // Preserve atomicity
    }

    default V putIfAbsent(@NotNull String key, V value) {
        return putIfAbsent(new CaseInsensitiveString(key), value);
    }

    default V computeIfAbsent(@NotNull String key, Supplier<V> valueSupplier) {
        return computeIfAbsent(new CaseInsensitiveString(key), k -> valueSupplier.get());
    }

    default V compute(@NotNull String key, UnaryOperator<V> remappingFunction) {
        return compute(new CaseInsensitiveString(key), (k, v) -> remappingFunction.apply(v));
    }

    default V computeIfPresent(@NotNull String key, UnaryOperator<V> remappingFunction) {
        return computeIfPresent(new CaseInsensitiveString(key), (k, v) -> remappingFunction.apply(v));
    }

    default V merge(@NotNull String key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return merge(new CaseInsensitiveString(key), value, remappingFunction);
    }

    /*
     * Additional utility methods
     */

    default boolean removeIf(@NotNull String key, @NotNull Predicate<V> filter) {
        AtomicBoolean removed = new AtomicBoolean(false);

        computeIfPresent(new CaseInsensitiveString(key), (k, value) -> {
            if (filter.test(value)) {
                removed.set(true);
                return null;
            } else {
                return value;
            }
        });

        return removed.get();
    }

    default boolean removeAllIf(@NotNull BiPredicate<CaseInsensitiveString, V> filter) {
        return entrySet().removeIf(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

}
