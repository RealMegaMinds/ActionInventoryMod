package megaminds.actioninventory.api.actionobjects;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.Helper;
import megaminds.actioninventory.api.helper.ObjectId;
import net.minecraft.util.Identifier;

public class ActionObjectHandler {
	private static final JsonParser PARSER = new JsonParser();
	private static final Gson GSON = new Gson();	//make builder, enable prettyprint
	private static final Map<Identifier, Supplier<ActionObject>> ALLOWED_TYPES;
	private static final Map<Identifier, Map<Identifier, ActionObject>> ACTION_OBJECTS;
	
	public static void registerActionObjectType(Identifier type, Supplier<ActionObject> supplier) {
		ALLOWED_TYPES.put(type, supplier);
	}
	
	public static ActionObject getNewActionObject(Identifier type) {
		return ALLOWED_TYPES.get(type).get();
	}
	
	public static ActionObject getActionObject(ObjectId id) {
		return ACTION_OBJECTS.get(id.type).get(id.id);
	}
	
	public static boolean hasActionObject(ObjectId id) {
		return ACTION_OBJECTS.containsKey(id.type) && ACTION_OBJECTS.get(id.type).containsKey(id.id);
	}
	
	public static void addActionObject(ActionObject object) {
		ACTION_OBJECTS.computeIfAbsent(object.getFullId().type, k->new HashMap<>()).put(object.getFullId().id, object);
	}
	
	public static void removeActionObject(ObjectId id) {
		Helper.ifNotNullDo(ACTION_OBJECTS.get(id.type), m->m.remove(id.id));
	}
	
	public static Set<Identifier> getAllOfType(Identifier type) {
		return ACTION_OBJECTS.get(type).keySet();
	}
	
	/**
	 * Called when the server is starting or when /actioninventory reload command is called.
	 */
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

	private static ActionObject doSingle(JsonObject jsonObj) {
		ActionObject obj = getNewActionObject(Helper.readTyped(jsonObj, Identifier::new));
		obj.readData(jsonObj);
		addActionObject(obj);
		return obj;
	}

	/**
	 * Called when the server is stopped.
	 */
	public static void stop(boolean save) {
		if (save) save();
		ACTION_OBJECTS.clear();
	}

	/**
	 * Called when the server is stopped and also periodically.
	 */
	public static void save() {
		ActionInventoryMod.info("Started saving ActionObjects");
		Map<String, JsonArray> objectsByPath = new HashMap<>();
		
		for (Map<Identifier, ActionObject> type : ACTION_OBJECTS.values()) {
			for (ActionObject obj : type.values()) {
				if (obj.isDirty()) {
					JsonObject jsonObj = new JsonObject();
					obj.writeData(jsonObj);
					objectsByPath.computeIfAbsent(addExtension(obj.getFileName(), ".json"), s->new JsonArray()).add(jsonObj);
				}
			}
		}
		
		objectsByPath.forEach(ActionObjectHandler::saveFile);
		ActionInventoryMod.info("Finished saving ActionObjects");
	}
	
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
		Helper.ifNotNullDo(Helper.warnIfException(()->Files.newBufferedWriter(file), "Failed to write to "+file), w->GSON.toJson(arr.size()==1?arr.get(0):arr, w));
	}
	
	private static String addExtension(String base, String ext) {
		return base.endsWith(ext) ? base : base+ext;
	}
	
	static {
		ACTION_OBJECTS = new HashMap<>();
		ALLOWED_TYPES = new HashMap<>();
	}
}