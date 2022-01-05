package megaminds.actioninventory.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.mixin.NbtCompoundMixin;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtNull;

/**
 * Just some random helper methods.
 */
public class Helper {
	private static final String GLOBAL = "GLOBAL", WORLD = "WORLD";
	
	public static <E> void forEach(E[] arr, Consumer<E> consumer) {
		for (E e : arr) {
			consumer.accept(e);
		}
	}
	
	/**
	 * Returns the first object in the given collection that matches the given predicate.<br>
	 * Returns null if none match.
	 */
	public static <E> E getFirst(Collection<E> col, Predicate<E> tester) {
		for (E e : col) {
			if (tester.test(e)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Returns the first object in the given array that matches the given predicate.<br>
	 * Returns null if none match.
	 */
	public static <E> E getFirst(E[] col, Predicate<E> tester) {
		for (E e : col) {
			if (tester.test(e)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Sets the position where the first object that matches the predicate to the given object.<br>
	 * Returns null if none match.
	 */
	public static <E> E setFirst(E[] col, Predicate<E> tester, E obj) {
		for (int i = 0; i < col.length; i++) {
			if (tester.test(col[i])) {
				E old = col[i];
				col[i] = obj;
				return old;
			}
		}
		return null;
	}
	
	/**
	 * Removes the first object in the given collection that matches the given predicate and returns it.<br>
	 * Returns null if none match.
	 */
	public static <E> E removeFirst(Collection<E> col, Predicate<E> tester) {
        for (Iterator<E> each = col.iterator(); each.hasNext();) {
        	E next = each.next();
            if (tester.test(next)) {
                each.remove();
                return next;
            }
        }
		return null;
	}
	
	/**
	 * If the given object is null or the object returned from the function is null, returns the given default object.
	 * If the given object is not null, returns the result of the given function.
	 */
	public static <E, R> R getOrDefault(E e, Function<E, R> func, R defaultObj) {
		return e!=null ? getOrDefault(func.apply(e), defaultObj) : defaultObj;
	}
	
	/**
	 * If the given object is null, returns the given default object.
	 * If the given object is not null, returns the given object.
	 */
    public static <E> E getOrDefault(E e, E defaultObj) {
        return e!=null ? e : defaultObj;
    }
    
//	
//	/**
//	 * If the given object is null, throws given error.
//	 * If the given object is not null, returns the result of the given function.
//	 */
//	public static <E, R, T extends RuntimeException> R getOrError(E e, Function<E, R> func, Supplier<T> error) {
//		if (e!=null) {
//			return func.apply(e);
//		}
//		throw error.get();
//	}
//	
	/**
	 * If the given object is null, does nothing. Returns false. <br>
	 * If the given object is not null, executes the given consumer. Returns true.
	 */
	public static <E> boolean ifDo(E e, Consumer<E> consumer) {
		if (e!=null) {
			consumer.accept(e);
			return true;
		}
		return false;
	}
	
	public static <K, V> void ifAndDo(K k, V v, BiConsumer<K, V> consumer) {
		if (k!=null&&v!=null) {
			consumer.accept(k, v);
		}
	}
	
	/**
	 * If the given object is null, does nothing.
	 * If the given object is not null, executes the given consumer.
	 */
	public static <E, R> void getDo(E e, Function<E, R> func, Consumer<R> consumer) {
		if (e!=null) {
			consumer.accept(func.apply(e));
		}
	}
	
	public static <C, E, R> void getDoForEach(C c, Function<C, Collection<E>> map, Function<E, R> func, Consumer<R> consumer) {
		getDo(c, map, arr->arr.forEach(e->getDo(e, func, consumer)));
	}
	
	public static <E, R> void getDoForEach(Iterable<E> c, Function<E, R> func, Consumer<R> consumer) {
		ifDo(c, arr->arr.forEach(e->getDo(e, func, consumer)));
	}
	
	public static <E, R> Collection<R> mapEach(Iterable<E> c, Function<E, R> func, R defaultObj) {
		if (c==null) return null;
		ArrayList<R> list = new ArrayList<>();
		c.forEach(e->list.add(getOrDefault(e, func::apply, defaultObj)));
		return list;
	}
	
	public static <K, V, R> Map<K, R> mapEach(Map<K, V> m, Function<V, R> func, R defaultObj) {
		if (m==null) return null;
		Map<K, R> map = new HashMap<>();
		m.forEach((k,v)->ifDo(getOrDefault(v, func, defaultObj), v2->map.put(k, v2)));
		return map;
	}
	
	public static <K, V, RK, RV> Map<RK, RV> mapEach(Map<K, V> m, Function<K, RK> keyFunc, Function<V, RV> valueFunc, RK defaultKey, RV defaultValue) {
		if (m==null) return null;
		Map<RK, RV> map = new HashMap<>();
		m.forEach((k,v)->ifAndDo(getOrDefault(k, keyFunc, defaultKey), getOrDefault(v, valueFunc, defaultValue), map::put));
		return map;
	}
	
	public static <E, R> Set<R> combineResults(Collection<E> vals, Function<E, Set<R>> func) {
		return vals.stream().flatMap(e->func.apply(e).stream()).collect(Collectors.toUnmodifiableSet());
	}
	
	/**
	 * Returns true if {@code o1} equals null or {@code o2}<br>
	 * Returns false if {@code o1} doesn't equal null nor {@code o2}
	 */
	public static boolean nullOrEquals(Object o1, Object o2) {
		return o1==null || o1.equals(o2);
	}
	
	public static <E> boolean nullOr(E o, Function<E, Boolean> func) {
		return o==null || func.apply(o);
	}
	
	public static <E> boolean notNullAnd(E o, Function<E, Boolean> func) {
		return o!=null && func.apply(o);
	}
	
	/**
	 * End/Number/String/Array/List: one.value equals two.value
	 * <br>
	 * Compound: foreach in one, compareNbt(one.values, two.values)
	 */
	public static boolean compareNbt(NbtElement valueToTestFor, NbtElement actualValue) {
		if (valueToTestFor==null || Objects.equals(valueToTestFor, actualValue) || valueToTestFor instanceof AbstractNbtNumber n1 && actualValue instanceof AbstractNbtNumber n2 && n1.numberValue().equals(n2.numberValue())) return true;
		if (actualValue==null) return false;
		
		if (valueToTestFor instanceof NbtCompound test && actualValue instanceof NbtCompound actual) {
			//Untested
//			Set<String> keys = c1.getKeys();
//			for (String s : keys) {
//				if (!compareNbt(c1.get(s), c2.get(s))) {
//					return false;
//				}
//			}
//			return true;
			
			//TODO Test
			NbtCompound temp = actual.copy();
			return temp.copyFrom(test).equals(temp);
		}
		
		return false;
	}
	
	public static <K, V extends NbtElement> NbtCompound createNbtCompound(Map<K, V> map) {
		return NbtCompoundMixin.createNbtCompound(Helper.mapEach(map, Object::toString, i->NbtNull.INSTANCE.equals(i)?null:i, null, null));
	}
	
	public static List<Path> resolvePaths(List<String> paths, Path global, Path server) {
		return paths.stream().map(s->resolvePath(s, global, server)).filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
	}
	
	public static Path resolvePath(String path, Path global, Path server) {
		Path p;
		if (path.startsWith(GLOBAL)) {
			p = global.resolve(path.substring(GLOBAL.length()+1));
		} else if (path.startsWith(WORLD)) {
			p = server.resolve(path.substring(WORLD.length()+1));
		} else {
			try {
				p = Path.of(path);
			} catch (InvalidPathException e) {
				p = null;
			}
		}
		return p!=null&&checkDir(p) ? p : null;
	}
	
	public static boolean checkDir(Path p) {
		try {
			Files.createDirectories(p);
			return true;
		} catch (IOException e) {
			ActionInventoryMod.warn("Couldn't create directory: "+p);
			return false;
		}
	}
}