package megaminds.actioninventory.api.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.Helper;
import megaminds.actioninventory.api.actionobjects.ActionObject;
import megaminds.actioninventory.api.helper.ObjectId;
import net.minecraft.util.Identifier;

public class ActionObjectHandler {
	private static final JsonParser PARSER = new JsonParser();
	private static final Map<Identifier, Supplier<ActionObject>> ALLOWED_TYPES;
	private static final Map<Identifier, Map<Identifier, ActionObject>> ACTION_OBJECTS;

	/**
	 * Registers a supplier for ActionObjects of a given type.
	 */
	public static void registerActionObjectType(Identifier type, Supplier<ActionObject> supplier) {
		ALLOWED_TYPES.put(type, supplier);
	}

	/**
	 * Returns a new ActionObject based on the given type.
	 */
	public static ActionObject getNewActionObject(Identifier type) {
		return ALLOWED_TYPES.get(type).get();
	}

	/**
	 * Returns the ActionObject with the given id.
	 */
	public static ActionObject getActionObject(ObjectId id) {
		return ACTION_OBJECTS.get(id.type).get(id.id);
	}

	/**
	 * Returns true if there is already an ActionObject with the given id.
	 */
	public static boolean hasActionObject(ObjectId id) {
		return ACTION_OBJECTS.containsKey(id.type) && ACTION_OBJECTS.get(id.type).containsKey(id.id);
	}

	/**
	 * Adds an ActionObject to keep track of.
	 */
	public static void addActionObject(ActionObject object) {
		ACTION_OBJECTS.computeIfAbsent(object.getId().type, k->new HashMap<>()).put(object.getId().id, object);
	}

	/**
	 * Removes the currently stored ActionObject with the given id.
	 */
	public static void removeActionObject(ObjectId id) {
		Helper.ifNotNullDo(ACTION_OBJECTS.get(id.type), m->m.remove(id.id));
	}

	/**
	 * This returns all of the ActionObjects of a given type. The returned set is unmodifiable.
	 */
	public static Set<Identifier> getAllOfType(Identifier type) {
		return Collections.unmodifiableSet(ACTION_OBJECTS.get(type).keySet());
	}

	/**
	 * Called when the server is starting or when /actioninventory reload command is called.<br>
	 */
	@Internal
	public static void load() {
		Path dir = ActionInventoryMod.getModRootDir();
		ActionInventoryMod.info("Loading ActionObject Files");
		Path[] files = Objects.requireNonNullElse(Helper.ifNotNullGet(Helper.warnIfException(()->Files.list(dir), "Failed to get files."), s->s.filter(p->p.toString().endsWith(".json")).toArray(Path[]::new)), new Path[0]);
		int count = 0;
		for (Path p : files) {
			String jsonStr = Helper.warnIfException(()->Files.readString(p), "Couldn't read file: "+p);
			if (jsonStr==null) continue;

			JsonElement jsonEl = PARSER.parse(jsonStr);
			count++;
			if (jsonEl.isJsonObject()) {
				doSingle(jsonEl.getAsJsonObject()).setFileName(p.getFileName().toString());
			} else {
				JsonArray arr = jsonEl.getAsJsonArray();
				for (JsonElement e : arr) {
					doSingle(e.getAsJsonObject()).setFileName(p.getFileName().toString());
					count++;
				}
			}
		}
		ActionInventoryMod.info("Loaded "+count+" Files");
	}

	/**
	 * Loads a single ActionObject from a JsonObject.
	 */
	private static ActionObject doSingle(JsonObject jsonObj) {
		ActionObject obj = getNewActionObject(Helper.readTyped(jsonObj, Identifier::new));
		obj.readData(jsonObj);
		addActionObject(obj);
		return obj;
	}

	/**
	 * Called when the server is stopped.
	 */
	@Internal
	public static void stop(boolean save) {
		if (save) save();
		ACTION_OBJECTS.clear();
	}

	/**
	 * Called when the server is stopped and also periodically.
	 */
	@Internal
	public static void save() {
		ActionInventoryMod.info("Started saving ActionObjects");
		Map<String, JsonArray> objectsByPath = new HashMap<>();
		ACTION_OBJECTS.values().stream().map(Map::values).map(Collection::stream).forEach(s->s.filter(ActionObject::isDirty).forEach(obj->{
			objectsByPath.computeIfAbsent(addExtension(obj.getFileName(), ".json"), str->new JsonArray()).add(ActionObject.writeData(obj));
		}));
		objectsByPath.forEach(ActionObjectHandler::saveFile);
		ActionInventoryMod.info("Finished saving ActionObjects");
	}

	/**
	 * Saves the given JsonArray to a file of the given name.
	 */
	private static void saveFile(String fileName, JsonArray arr) {
		if (arr.size()==0) return;
		Path file = ActionInventoryMod.getModRootDir().resolve(fileName);
		if (Files.notExists(file) && Helper.warnIfException(()->Files.createFile(file), "Couldn't create save file: "+file)==null) {
			return;
		}
		if (!Files.isRegularFile(file)) {
			ActionInventoryMod.log(Level.WARN, "Path: "+file+" needs to be a file.");
			return;
		}
		try (JsonWriter w = new JsonWriter(Files.newBufferedWriter(file))) {
			w.setIndent("  ");
			Streams.write(arr.size()==1?arr.get(0):arr, w);
		} catch (IOException e) {
			ActionInventoryMod.log(Level.WARN, "Failed to write ActionObjects to "+file);
			e.printStackTrace();
		}
	}

	/**
	 * Concats {@code ext} onto the end of {@code base} if {@code base} doesn't already end with {@code ext}
	 */
	private static String addExtension(String base, String ext) {
		return base.endsWith(ext) ? base : base+ext;
	}

	static {
		ACTION_OBJECTS = new HashMap<>();
		ALLOWED_TYPES = new HashMap<>();
	}
}