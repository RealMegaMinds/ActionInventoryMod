package megaminds.actioninventory.serialization;

import static megaminds.actioninventory.util.JsonHelper.getOrDefault;

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
import megaminds.actioninventory.util.JsonHelper;
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

		BasicAction callback = getOrDefault(obj.get(CALLBACK), BasicAction.class, context::deserialize, null);
		ItemStack[] stacks = getOrDefault(obj.get(ITEMS), l->JsonHelper.toList(l.getAsJsonArray(), e->context.deserialize(e, ItemStack.class)).toArray(ItemStack[]::new), new ItemStack[] {ItemStack.EMPTY});
		int interval = getOrDefault(obj.get(INTERVAL), JsonElement::getAsInt, 1);
		boolean random = getOrDefault(obj.get(RANDOM), JsonElement::getAsBoolean, false);

		return new AccessableAnimatedGuiElement(stacks, interval, random, callback);
	}
}