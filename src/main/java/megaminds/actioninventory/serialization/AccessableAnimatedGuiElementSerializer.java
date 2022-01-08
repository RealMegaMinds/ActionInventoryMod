package megaminds.actioninventory.serialization;

import static megaminds.actioninventory.util.JsonHelper.*;

import java.lang.reflect.Type;
import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.gui.AccessableAnimatedGuiElement;
import net.minecraft.item.ItemStack;

public class AccessableAnimatedGuiElementSerializer implements JsonDeserializer<AccessableAnimatedGuiElement>, JsonSerializer<AccessableAnimatedGuiElement> {
	private static final String CALLBACK = "callback", ITEMS = "items";
	private static final String RANDOM = "isRandom", INTERVAL = "interval";
	
	@Override
	public JsonElement serialize(AccessableAnimatedGuiElement src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		JsonArray items = Arrays.stream(src.getItems()).map(context::serialize).collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
		obj.add(ITEMS, items);
		obj.addProperty(INTERVAL, src.getInterval());
		obj.addProperty(RANDOM, src.getRandom());
		obj.add(CALLBACK, context.serialize(src.getGuiCallback()));
		return obj;
	}
	
	@Override
	public AccessableAnimatedGuiElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();

		BasicAction callback = clazz(obj.get(CALLBACK), BasicAction.class, context);
		ItemStack[] stacks = clazzArr(obj.get(ITEMS), ItemStack.class, context);
		int interval = integer(obj.get(INTERVAL), 1);
		boolean random = bool(obj.get(RANDOM));

		return new AccessableAnimatedGuiElement(stacks, interval, random, callback);
	}
}