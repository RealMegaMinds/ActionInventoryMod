package megaminds.actioninventory.api.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import megaminds.actioninventory.api.util.ObjectId;
import net.minecraft.util.Identifier;

public class ActionObjectHandler {
	private static final JsonParser PARSER = new JsonParser();
	private static final Map<Identifier, Supplier<ActionObject>> ALLOWED_TYPES = new HashMap<>();
	private static final List<ActionObjectHandler> HANDLERS = new ArrayList<>();
	
	private final Map<Identifier, Map<Identifier, ActionObject>> ACTION_OBJECTS;
	private final Path path;
	private final String id;
	
	public ActionObjectHandler(Path path, String id) {
		this.path = path;
		this.id  = id;
		ACTION_OBJECTS = new HashMap<>();
		HANDLERS.add(this);
	}
	
	/**
	 * Returns an unmodifiable view of the list of ActionObjectHandlers.
	 */
	public static List<ActionObjectHandler> getHandlers() {
		return Collections.unmodifiableList(HANDLERS);
	}
	
	public String getId() {
		return id;
	}
	
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
	public ActionObject getActionObject(ObjectId id) {
		return ACTION_OBJECTS.get(id.type).get(id.id);
	}
	
	/**
	 * Returns the ActionObject with the given id.
	 * Searches through all ActionObjectHandlers.
	 */
	public static ActionObject getActionObjectx(ObjectId id) {
		for (ActionObjectHandler h : HANDLERS) {
			ActionObject a = h.getActionObject(id);
			if (a!=null) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Returns true if there is already an ActionObject with the given id.
	 */
	public boolean hasActionObject(ObjectId id) {
		return ACTION_OBJECTS.containsKey(id.type) && ACTION_OBJECTS.get(id.type).containsKey(id.id);
	}
	/**
	 * Returns true if there is already an ActionObject with the given id.
	 * Searches through all ActionObjectHandlers.
	 */
	public static boolean hasActionObjectx(ObjectId id) {
		return Helper.getFirst(HANDLERS, h->h.hasActionObject(id))!=null;
	}

	/**
	 * Adds an ActionObject to keep track of.
	 */
	public void addActionObject(ActionObject object) {
		ACTION_OBJECTS.computeIfAbsent(object.getId().type, k->new HashMap<>()).put(object.getId().id, object);
	}

	/**
	 * Removes the currently stored ActionObject with the given id.
	 */
	public void removeActionObject(ObjectId id) {
		Helper.ifNotNullDo(ACTION_OBJECTS.get(id.type), m->m.remove(id.id));
	}
	/**
	 * Removes the currently stored ActionObject with the given id.
	 * Searches through all ActionObjectHandlers.
	 */
	public static void removeActionObjectx(ObjectId id) {
		for (ActionObjectHandler h : HANDLERS) {
			if (Helper.ifNotNullGet(h.ACTION_OBJECTS.get(id.type), m->m.remove(id.id))!=null) {
				return;
			}
		}
	}

	/**
	 * This returns all of the ActionObjects of a given type. The returned set is unmodifiable.
	 */
	public Set<Identifier> getAllOfType(Identifier type) {
		return Collections.unmodifiableSet(ACTION_OBJECTS.get(type).keySet());
	}
	
	/**
	 * This returns all of the ActionObjects of a given type. The returned set is unmodifiable.
	 * Searches through all ActionObjectHandlers.
	 */
	public static Set<Identifier> getAllOfTypex(Identifier type) {
		
		return null;
	}

	/**
	 * Loads all ActionObjects at the path.
	 */
	public void load() {
		ActionInventoryMod.info(id+": Loading ActionObject Files");
		Path[] files = Objects.requireNonNullElse(Helper.ifNotNullGet(Helper.warnIfException(()->Files.list(path), "Failed to get files."), s->s.filter(p->p.toString().endsWith(".json")).toArray(Path[]::new)), new Path[0]);
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
		ActionInventoryMod.info(id+": Loaded "+count+" Files");
	}

	/**
	 * Loads a single ActionObject from a JsonObject.
	 */
	private ActionObject doSingle(JsonObject jsonObj) {
		ActionObject obj = getNewActionObject(Helper.readTyped(jsonObj, Identifier::new));
		obj.readData(jsonObj);
		addActionObject(obj);
		return obj;
	}

	/**
	 * Saves and clears ActionObjects.
	 */
	public void stop() {
		save();
		ACTION_OBJECTS.clear();
	}

	/**
	 * Saves ActionObjects to files.
	 */
	public void save() {
		ActionInventoryMod.info(id+": Started saving ActionObjects");
		Map<String, JsonArray> objectsByPath = new HashMap<>();
		ACTION_OBJECTS.values().stream().map(Map::values).map(Collection::stream).forEach(s->s.filter(ActionObject::isDirty).forEach(obj->{
			objectsByPath.computeIfAbsent(addExt(obj.getFileName()), str->new JsonArray()).add(ActionObject.writeData(obj));
		}));
		objectsByPath.forEach((s, arr)->saveFile(path.resolve(s), arr));
		ActionInventoryMod.info(id+": Finished saving ActionObjects");
	}

	/**
	 * Saves the given JsonArray to a file of the given name.
	 */
	private static void saveFile(Path file, JsonArray arr) {
		if (arr.size()==0) return;
		if (Files.notExists(file) && Helper.warnIfException(()->Files.createFile(file), "Couldn't create save file: "+file)==null) {
			return;
		}
		if (!Files.isRegularFile(file)) {
			ActionInventoryMod.log(Level.WARN, "Couldn't save to: "+file);
			return;
		}
		try (JsonWriter w = new JsonWriter(Files.newBufferedWriter(file))) {
			w.setIndent("  ");
			Streams.write(arr.size()==1?arr.get(0):arr, w);
		} catch (IOException e) {
			ActionInventoryMod.log(Level.WARN, "Failed to save ActionObjects to "+file);
			e.printStackTrace();
		}
	}

	/**
	 * Concats ".json" onto the end of {@code base} if {@code base} doesn't already end with {@code ext}
	 */
	private static String addExt(String base) {
		return base.endsWith(".json") ? base : base+".json";
	}
	
	/**
	 * Returns true if directory exists.
	 * Returns false if directory doesn't exist and prints warning.
	 */
	private static boolean checkDirExists(Path dir) {
		if (dir==null) return false;
		if (!Files.isDirectory(dir)) {
			ActionInventoryMod.log(Level.WARN, "Directory doesn't exist or is not a directory: "+dir);
			return false;
		}
		return true;
	}
}