package megaminds.actioninventory.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * Replaces null values with def. Will not return null.
 */
@SuppressWarnings("serial")
public class DefaultedList<E> implements Int2ObjectMap<E>, List<E> {
	private final E def;

	public DefaultedList(E defObj) {
		Objects.requireNonNull(defObj, "defObj cannot be null");
		def = defObj;
		new Int2ObjectArrayMap<>().
	}

	public DefaultedList(E defObj, int initialCapacity) {
		super(initialCapacity);
		Objects.requireNonNull(defObj, "defObj cannot be null");
		def = defObj;
	}

	public E getDefault() {
		return def;
	}

	@Override
	public E get(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends E> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E set(int index, E element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(int index, E element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsKey(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E defaultReturnValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void defaultReturnValue(E arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ObjectSet<Entry<E>> int2ObjectEntrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ObjectCollection<E> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public boolean addAll(Collection<? extends E> c) {
//		c.forEach(this::add);
//		return true;
//	}
//
//	@Override
//	public boolean addAll(int index, Collection<? extends E> c) {
//		for (var e : c) {
//			add(index++, e);
//		}
//		return true;
//	}
//
//	@Override
//	public boolean add(E e) {
//		if (e==null) e = def;
//		return super.add(e);
//	}
//
//	@Override
//	public E set(int index, E element) {
//		if (element==null) element = def;
//		return super.set(index, element);
//	}
//
//	@Override
//	public void add(int index, E element) {
//		if (element==null) element = def;
//		super.add(index, element);
//	}
//
//	@Override
//	public int indexOf(Object o) {
//		if (o==null) return -1;
//		return super.indexOf(o);
//	}
//
//	@Override
//	public int lastIndexOf(Object o) {
//		if (o==null) return -1;
//		return super.lastIndexOf(o);
//	}
}