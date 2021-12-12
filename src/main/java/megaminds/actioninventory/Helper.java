package megaminds.actioninventory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;

/**
 * Just some random helper methods.
 */
public class Helper {
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
	
	public static <E> E readTyped(JsonObject obj, Function<String, E> func) {
		return func.apply(obj.get("type").getAsString());
	}
	
	public static <E> E warnIfException(ExceptionSupplier<E> sup, String msg) {
		try {
			return sup.get();
		} catch (Exception e) {
			ActionInventoryMod.log(Level.WARN, msg);
			return null;
		}
	}
	
	public static <E, R> Set<R> combineResults(Collection<E> vals, Function<E, Set<R>> func) {
		Collection<R> combined = new HashSet<>();
		return vals.stream().flatMap(e->func.apply(e).stream()).collect(Set::collect);
	}
}