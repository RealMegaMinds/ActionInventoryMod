package megaminds.actioninventory.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import megaminds.actioninventory.ActionInventoryMod;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

/**
 * Just some random helper methods.
 */
public class Helper {
	private static final String GLOBAL = "GLOBAL", WORLD = "WORLD";

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
	 * If the given object is null, returns null.
	 * If the given object is not null, returns the result of the given function.
	 */
	public static <E, R> R ifNotNullGet(E e, Function<E, R> func) {
		return e==null||func==null ? null : func.apply(e);
	}
	/**
	 * If the given object is null, does nothing.
	 * If the given object is not null, executes the given consumer.
	 */
	public static <E> void ifNotNullDo(E e, Consumer<E> consumer) {
		if (e!=null&&consumer!=null) {
			consumer.accept(e);
		}
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
	
	public static <E> List<E> toList(JsonArray arr, Function<JsonElement, E> map) {
		List<E> list = new ArrayList<>(arr.size());
		arr.forEach(e->list.add(map.apply(e)));
		return list;
	}
	
	/**
	 * End/Number/String/Array/List: one.value equals two.value
	 * <br>
	 * Compound: foreach in one, compareNbt(one.values, two.values)
	 */
	public static boolean compareNbt(NbtElement one, NbtElement two) {
		if (one==null || Objects.equals(one, two) || one instanceof AbstractNbtNumber n1 && two instanceof AbstractNbtNumber n2 && n1.numberValue().equals(n2.numberValue())) return true;
		if (two==null) return false;
		
		if (one instanceof NbtCompound c1 && two instanceof NbtCompound c2) {
			Set<String> keys = c1.getKeys();
			for (String s : keys) {
				if (!compareNbt(c1.get(s), c2.get(s))) {
					return false;
				}
			}
			return true;
		}
		
		return false;
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