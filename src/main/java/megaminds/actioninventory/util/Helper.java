package megaminds.actioninventory.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import eu.pb4.placeholders.api.PlaceholderContext;
import megaminds.actioninventory.ActionInventoryMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Just some random helper methods.
 */
public class Helper {
	private static final String GLOBAL = "GLOBAL";
	private static final String WORLD = "WORLD";

	private Helper() {}

	/**
	 * Parses nbt of the item stack. The same stack is returned with the changes.
	 */
	public static ItemStack parseItemStack(ItemStack stack, PlaceholderContext context) {
		if (stack.hasNbt()) {
			NbtPlaceholderParser.replaceCompound(context, stack.getNbt());
			stack.getItem().postProcessNbt(stack.getNbt());
		}

		return stack;
	}

	/**
	 * False if compound==null
	 */
	public static boolean getBoolean(@Nullable NbtCompound compound, String key) {
		return compound!=null && compound.getBoolean(key);
	}

	/**
	 * 0 if compound==null
	 */
	public static int getInt(@Nullable NbtCompound compound, String key) {
		return compound==null ? 0 : compound.getInt(key);
	}

	/**
	 * 0 if compound==null
	 */
	public static long getLong(@Nullable NbtCompound compound, String key) {
		return compound==null ? 0 : compound.getInt(key);
	}

	/**
	 * Throws error if there is no player for the UUID.
	 */
	@NotNull
	public static ServerPlayerEntity getPlayer(MinecraftServer server, UUID playerUuid) {
		var player = server.getPlayerManager().getPlayer(playerUuid);
		//TODO add toggle for this error
		Objects.requireNonNull(player, ()->"No Player Exists for UUID: "+playerUuid);
		return player;
	}

	public static int getTotalExperienceForLevel(int level) {
		if (level<17) {
			return level*level + 6*level;
		} else if (level<32) {
			return (int) (2.5*level*level - 40.5*level + 360);
		} else {
			return (int) (4.5*level*level - 162.5*level + 2220);
		}
	}

	@SuppressWarnings("unchecked")
	public static <R extends NbtElement> R computeIfAbsent(NbtCompound holder, Function<String, R> creator, String key) {
		if (holder.contains(key)) {
			return (R) holder.get(key);
		} else {
			var r = creator.apply(key);
			holder.put(key, r);
			return r;
		}
	}

	public static <E> boolean containsAny(Collection<E> col, Predicate<E> tester) {
		return getFirst(col, tester)!=null;
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

	public static <E> boolean ifDo(E e, Consumer<E> consumer) {
		if (e!=null) {
			consumer.accept(e);
			return true;
		}
		return false;
	}

	public static <E> boolean ifOrDo(E e, Predicate<E> test, Consumer<E> consumer) {
		if (e!=null || test.test(e)) {
			consumer.accept(e);
			return true;
		}
		return false;
	}

	public static <K, V, R> Map<K, R> mapEach(Map<K, V> m, Function<V, R> func, R defaultObj, boolean allowNull) {
		if (m==null) return null;	//NOSONAR Part of contract

		Map<K, R> map = new HashMap<>();
		for (Entry<K, V> e : m.entrySet()) {
			R r = apply(e.getValue(), func, defaultObj);
			if (allowNull || r!=null) {
				map.put(e.getKey(), r);
			}
		}
		return map;
	}

	public static <K, V, R1, R2> Map<R1, R2> mapEach(Map<K, V> m, Function<K, R1> keyFunc, Function<V, R2> valueFunc, R1 defaultKey, R2 defaultValue, boolean allowNull) {
		if (m==null) return null;	//NOSONAR Part of contract

		Map<R1, R2> map = new HashMap<>();
		for (Entry<K, V> e : m.entrySet()) {
			R1 rk = apply(e.getKey(), keyFunc, defaultKey);
			R2 rv = apply(e.getValue(), valueFunc, defaultValue);
			if (allowNull || rk!=null&&rv!=null) {
				map.put(rk, rv);
			}
		}
		return map;
	}

	public static <E> boolean notNullAnd(E o, Predicate<E> func) {
		return o!=null && func.test(o);
	}

	public static <V extends NbtElement> NbtCompound mapToCompound(Map<String, V> map) {
		NbtCompound c = new NbtCompound();
		map.forEach(c::put);
		return c;
	}

	/**
	 * Assumes contents of compound are all of type V.
	 */
	@SuppressWarnings("unchecked")
	public static <V extends NbtElement> Map<String, V> compoundToMap(NbtCompound c) {
		Set<String> keys = c.getKeys();
		Map<String, V> map =  new HashMap<>(keys.size());
		for (String s : keys) {
			map.put(s, (V)c.get(s));
		}
		return map;
	}

	public static List<Path> resolvePaths(List<String> paths, Path global, Path server) {
		return paths.stream().map(s->resolvePath(s, global, server)).filter(Objects::nonNull).toList();
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
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns null if from is null.
	 */
	public static <E, R> R apply(E from, Function<E, R> func) {
		return apply(from, func, (R)null);
	}

	/**
	 * Returns def if from is null.
	 */
	public static <E, R> R apply(E from, Function<E, R> func, R def) {
		return from!=null ? func.apply(from) : def;
	}

	/**
	 * Returns def.get() if from is null
	 */
	public static <E, R> R apply(E from, Function<E, R> func, Supplier<R> def) {
		return from!=null ? func.apply(from) : def.get();
	}
}