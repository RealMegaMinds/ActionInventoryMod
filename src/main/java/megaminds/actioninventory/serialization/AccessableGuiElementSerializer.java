package megaminds.actioninventory.serialization;

import static megaminds.actioninventory.util.JsonHelper.*;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.gui.AccessableGuiElement;
import net.minecraft.item.ItemStack;

public class AccessableGuiElementSerializer implements JsonDeserializer<AccessableGuiElement>, JsonSerializer<AccessableGuiElement> {
	private static final String CALLBACK = "callback", ITEM = "item";
	
	@Override
	public AccessableGuiElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		BasicAction callback = clazz(obj.get(CALLBACK), BasicAction.class, context);
		ItemStack item = clazz(obj.get(ITEM), ItemStack.class, context, ItemStack.EMPTY);
		return new AccessableGuiElement(item, callback);
	}
	
	@Override
	public JsonElement serialize(AccessableGuiElement src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.add(ITEM, context.serialize(src.getItemStackInternalUseOnly()));
		obj.add(CALLBACK, context.serialize(src.getGuiCallback()));
		return obj;
	}
}