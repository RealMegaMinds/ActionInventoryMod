/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ForwardingSet<E> implements Set<E> {

    private final Set<E> delegate;

    protected ForwardingSet(Set<E> delegate) {
        this.delegate = delegate;
    }

    /**
     * Returns the mutex object to synchronize on, to be used when the delegate is created with
     * {@link java.util.Collections#synchronizedSet(Set)}.
     *
     * The method can be exposed by subclasses when necessary.
     */
    protected Object getMutex() {
        return delegate;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object element) {
        return delegate.contains(element);
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T @NotNull [] array) {
        return delegate.toArray(array);
    }

    @Override
    public boolean add(E element) {
        return delegate.add(element);
    }

    @Override
    public boolean remove(Object element) {
        return delegate.remove(element);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return delegate.containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> collection) {
        return delegate.addAll(collection);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        return delegate.retainAll(collection);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        return delegate.removeAll(collection);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Spliterator<E> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return delegate.removeIf(filter);
    }

    @Override
    public Stream<E> stream() {
        return delegate.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return delegate.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        delegate.forEach(action);
    }

}
