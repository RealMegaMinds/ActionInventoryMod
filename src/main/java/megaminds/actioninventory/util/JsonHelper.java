package megaminds.actioninventory.util;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class JsonHelper {
	private JsonHelper() {}

	public static boolean isNull(JsonElement e) {
		return e==null || e.isJsonNull();
	}

	public static boolean notNull(JsonElement e) {
		return e!=null&&!e.isJsonNull();
	}

	public static <E> E notNull(E e, E def) {
		return e!=null ? e : def;
	}

	public static <E> E notNull(JsonElement e, Function<JsonElement, E> func, String error) {
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

	public static <R> R orError(JsonElement e, Function<JsonElement, R> func, String error) {
		if (e==null) throw new JsonParseException(error);
		return func.apply(e);
	}

	//GET AS TYPE HELPERS
	@NotNull
	public static JsonArray array(JsonElement e) {
		return array(e, JsonArray::new);
	}

	public static JsonArray array(JsonElement e, Supplier<JsonArray> def) {
		if (notNull(e)) {
			if (e.isJsonArray()) {
				return e.getAsJsonArray();
			} else {
				JsonArray arr = new JsonArray(1);
				arr.add(e);
				return arr;
			}
		}
		return def.get();
	}

	/**
	 * Returns null if e is null.
	 */
	public static String string(JsonElement e) {
		return string(e, null);
	}

	public static String string(JsonElement e, String def) {
		return notNull(e) ? e.getAsString() : def;
	}

	/**
	 * Returns null if e is null.
	 */
	public static Identifier identifier(JsonElement e) {
		return identifier(e, (Identifier)null);
	}

	public static Identifier identifier(JsonElement e, Identifier def) {
		return notNull(e) ? new Identifier(e.getAsString()) : def;
	}

	public static Identifier identifier(JsonElement e, Supplier<Identifier> def) {
		return notNull(e) ? new Identifier(e.getAsString()) : def.get();
	}

	/**
	 * Returns 0 if e is null.
	 */
	public static int integer(JsonElement e) {
		return integer(e, 0);
	}

	public static int integer(JsonElement e, int def) {
		return notNull(e) ? e.getAsInt() : def;
	}

	public static int integer(JsonElement e, IntSupplier def) {
		return notNull(e) ? e.getAsInt() : def.getAsInt();
	}

	/**
	 * Returns 0 if e is null.
	 */

	public static float floatt(JsonElement e) {
		return floatt(e, 0);
	}

	public static float floatt(JsonElement e, float def) {
		return notNull(e) ? e.getAsFloat() : def;
	}

	/**
	 * Returns false if e is null.
	 */
	public static boolean bool(JsonElement e) {
		return bool(e, false);
	}

	public static boolean bool(JsonElement e, boolean def) {
		return notNull(e) ? e.getAsBoolean() : def;
	}

	public static <E> Optional<E> optional(JsonElement e, JsonDeserializationContext context, Type inner) {
		return context.deserialize(e, TypeToken.getParameterized(Optional.class, inner).getType());
	}

	/**
	 * Returns {@link TextContent#empty()} if e is null.
	 */
	@NotNull
	public static Text text(JsonElement e) {
		return text(e, Text.empty());
	}

	public static Text text(JsonElement e, Text def) {
		return notNull(e) ? notNull(Text.Serializer.fromJson(e), def) : def;
	}

	/**
	 * Returns null if e is null.
	 */
	public static <E> E clazz(JsonElement e, Class<E> clazz, JsonDeserializationContext context) {
		return clazz(e, clazz, context, (E)null);
	}

	public static <E> E clazz(JsonElement e, Class<E> clazz, JsonDeserializationContext context, E def) {
		return notNull(e) ? context.deserialize(e, clazz) : def;
	}

	public static <E> E clazz(JsonElement e, Class<E> clazz, JsonDeserializationContext context, Supplier<E> def) {
		return notNull(e) ? context.deserialize(e, clazz) : def.get();
	}

	/**
	 * Returns null if e is null.
	 */
	public static <E> E custom(JsonElement e, Function<JsonElement, E> func) {
		return custom(e, func, (E)null);
	}

	public static <E> E custom(JsonElement e, Function<JsonElement, E> func, E def) {
		return notNull(e) ? func.apply(e) : def;
	}

	public static <E> E custom(JsonElement e, Function<JsonElement, E> func, Supplier<E> def) {
		return notNull(e) ? func.apply(e) : def.get();
	}

	@NotNull
	public static List<String> stringList(JsonElement e, boolean allowNull) {
		List<String> list = new ArrayList<>();
		array(e).forEach(s->Helper.ifOrDo(string(s), t->allowNull, list::add));
		return list;
	}

	@NotNull
	public static <E> List<E> clazzList(JsonElement e, Class<E> clazz, JsonDeserializationContext context, boolean allowNull) {
		List<E> list = new ArrayList<>();
		array(e).forEach(c->Helper.ifOrDo(clazz(c, clazz, context), t->allowNull, list::add));
		return list;
	}

	@NotNull
	public static <E> List<E> customList(JsonElement e, Function<JsonElement, E> func, boolean allowNull) {
		List<E> list = new ArrayList<>();
		array(e).forEach(c->Helper.ifOrDo(custom(c, func), t->allowNull, list::add));
		return list;
	}

	@NotNull
	public static <E, R extends List<E>> R customList(JsonElement e, Function<JsonElement, E> func, boolean allowNull, R list) {
		array(e).forEach(c->Helper.ifOrDo(custom(c, func), t->allowNull, list::add));
		return list;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public static <E> E[] clazzArr(JsonElement e, Class<E> clazz, JsonDeserializationContext context, boolean allowNull) {
		return clazzList(e, clazz, context, allowNull).toArray(i->(E[])Array.newInstance(clazz, i));
	}

	@NotNull
	public static <E> E[] clazzArr(JsonElement e, Class<E> clazz, JsonDeserializationContext context) {
		return clazzArr(e, clazz, context, false);
	}

	@NotNull
	public static <E> Stream<E> clazzStream(JsonElement e, Class<E> clazz, JsonDeserializationContext context, boolean allowNull) {
		return clazzList(e, clazz, context, allowNull).stream();
	}

	@NotNull
	public static <E> Stream<E> clazzStream(JsonElement e, Class<E> clazz, JsonDeserializationContext context) {
		return clazzStream(e, clazz, context, false);
	}
}