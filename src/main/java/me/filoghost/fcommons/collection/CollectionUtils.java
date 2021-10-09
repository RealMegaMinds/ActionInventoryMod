/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class CollectionUtils {

    @Contract("null -> null; !null -> !null")
    public static <E> List<E> newArrayList(@Nullable Collection<E> collection) {
        if (collection == null) {
            return null;
        }

        return new ArrayList<>(collection);
    }

    @Contract("null -> null; !null -> !null")
    public static <E> Set<E> newHashSet(@Nullable Collection<E> collection) {
        if (collection == null) {
            return null;
        }

        return new HashSet<>(collection);
    }

    @Contract("null -> null; !null -> !null")
    public static <K, V> Map<K, V> newHashMap(@Nullable Map<K, V> map) {
        if (map == null) {
            return null;
        }

        return new HashMap<>(map);
    }

    @Contract("null -> null; !null -> !null")
    public static <E> ImmutableList<E> newImmutableList(@Nullable Collection<E> collection) {
        if (collection == null) {
            return null;
        }

        return ImmutableList.copyOf(collection);
    }

    @Contract("null -> null; !null -> !null")
    public static <E> ImmutableSet<E> newImmutableSet(@Nullable Collection<E> collection) {
        if (collection == null) {
            return null;
        }

        return ImmutableSet.copyOf(collection);
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    public static <A, B> List<B> toArrayList(@Nullable Collection<A> collection, @NotNull Function<A, B> transformFunction) {
        if (collection == null) {
            return null;
        }

        List<B> output = new ArrayList<>(collection.size());
        for (A element : collection) {
            output.add(transformFunction.apply(element));
        }
        return output;
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    public static <A, B> Set<B> toHashSet(@Nullable Collection<A> collection, @NotNull Function<A, B> transformFunction) {
        if (collection == null) {
            return null;
        }

        // Taken from the HashSet constructor that has a Collection parameter, to fit initial items
        int initialSize = Math.max((int) (collection.size() / 0.75f) + 1, 16);

        Set<B> output = new HashSet<>(initialSize);
        for (A element : collection) {
            output.add(transformFunction.apply(element));
        }
        return output;
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    public static <A, B> ImmutableList<B> toImmutableList(@Nullable Collection<A> collection, @NotNull Function<A, B> transformFunction) {
        if (collection == null) {
            return null;
        }

        ImmutableList.Builder<B> builder = ImmutableList.builder();
        for (A element : collection) {
            builder.add(transformFunction.apply(element));
        }
        return builder.build();
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    public static <A, B> ImmutableSet<B> toImmutableSet(@Nullable Collection<A> collection, @NotNull Function<A, B> transformFunction) {
        if (collection == null) {
            return null;
        }

        ImmutableSet.Builder<B> builder = ImmutableSet.builder();
        for (A element : collection) {
            builder.add(transformFunction.apply(element));
        }
        return builder.build();
    }

}
