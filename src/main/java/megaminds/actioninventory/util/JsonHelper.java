package megaminds.actioninventory.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonHelper {	
	public static boolean isNull(JsonElement e) {
		return e==null || e.isJsonNull();
	}
	
	public static boolean notNull(JsonElement e) {
		return e!=null&&!e.isJsonNull();
	}
	
	/**
	 * If the given object is null or the object returned from the function is null, returns the given default object.
	 * If the given object is not null, returns the result of the given function.
	 */
	public static <R> R getOrDefault(JsonElement e, Function<JsonElement, R> func, R defaultObj) {
		return notNull(e) ? Helper.getOrDefault(func.apply(e), defaultObj) : defaultObj;
	}
	
	public static <R> R getOrDefault(JsonElement e, Class<R> clazz, BiFunction<JsonElement, Class<R>, R> func, R defaultObj) {
		return notNull(e) ? Helper.getOrDefault(func.apply(e, clazz), defaultObj) : defaultObj;
	}
	
	/**
	 * If the given object is null or the object returned from the function is null, returns the given default object.
	 * If the given object is not null, returns the result of the given function.
	 */
	public static <R> R getOrDefault(JsonElement e, Function<JsonElement, R> func, Supplier<R> defaultObj) {
		return notNull(e) ? Helper.getOrDefault(func.apply(e), defaultObj) : defaultObj.get();
	}
	
	/**
	 * If the given object is null, throws given error.
	 * If the given object is not null, returns the result of the given function.
	 */
	public static <R> R getOrError(JsonElement e, Function<JsonElement, R> func, String error) {
		if (notNull(e)) {
			return func.apply(e);
		}
		throw new JsonParseException(error);
	}
	
	/**
	 * If the given object is null, does nothing. Returns false. <br>
	 * If the given object is not null, executes the given consumer. Returns true.
	 */
	public static <E extends JsonElement> boolean ifDo(E e, Consumer<E> consumer) {
		if (notNull(e)) {
			consumer.accept(e);
			return true;
		}
		return false;
	}
	
	/**
	 * If the given object is not null and the result of the function is not null, executes the given consumer.
	 * Otherwise does nothing.
	 */
	public static <R> void getDo(JsonElement e, Function<JsonElement, R> func, Consumer<R> consumer) {
		R temp;
		if (notNull(e) && (temp=func.apply(e))!=null) {
			consumer.accept(temp);
		}
	}
	
	public static <R> void getDo(JsonElement e, Class<R> clazz, BiFunction<JsonElement, Class<R>, R> func, Consumer<R> consumer) {
		R temp;
		if (notNull(e) && (temp=func.apply(e, clazz))!=null) {
			consumer.accept(temp);
		}
	}
	
	/**
	 * Converts the given object into an array.
	 * Executes {@link #getDo(JsonElement, Function, Consumer)} for each element in the array.
	 * If the object was null, does nothing.
	 */
	public static <R> void getDoForEach(JsonElement a, Function<JsonElement, R> func, Consumer<R> consumer) {
		ifDo(arrayOf(a), arr->arr.forEach(e->getDo(e, func, consumer)));
	}
	
	public static <R> void getDoForEach(JsonElement a, Class<R> clazz, BiFunction<JsonElement, Class<R>, R> func, Consumer<R> consumer) {
		ifDo(arrayOf(a), arr->arr.forEach(e->getDo(e, clazz, func, consumer)));
	}
	
	public static <R> Collection<R> getForEach(JsonElement a, Function<JsonElement, R> func) {
		List<R> list = new ArrayList<>();
		getDoForEach(a, func, list::add);
		return list;
	}
	
	public static <R> Collection<R> getForEach(JsonElement a, Class<R> clazz, BiFunction<JsonElement, Class<R>, R> func) {
		List<R> list = new ArrayList<>();
		getDoForEach(a, clazz, func, list::add);
		return list;
	}
	
	public static JsonArray arrayOf(JsonElement el) {
		if (notNull(el)) {
			if (el.isJsonArray()) {
				return el.getAsJsonArray();
			} else {
				JsonArray arr = new JsonArray(1);
				arr.add(el);
				return arr;
			}
		}
		return null;
	}
	
	/**
	 * Returns a new list containing the results of applying the given function to each JsonElement in the given array.
	 */
	public static <R> List<R> toList(JsonArray arr, Function<JsonElement, R> map) {
		List<R> list = new ArrayList<>(arr.size());
		arr.forEach(e->list.add(map.apply(e)));
		return list;
	}
}