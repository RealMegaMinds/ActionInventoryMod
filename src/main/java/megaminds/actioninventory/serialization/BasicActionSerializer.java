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

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.actions.Action;
import megaminds.actioninventory.actions.BasicAction;
import net.minecraft.screen.slot.SlotActionType;

public class BasicActionSerializer implements JsonDeserializer<BasicAction>, JsonSerializer<BasicAction> {
	private static final String INDEX = "index", CLICK_TYPE = "clickType", ACTION_TYPE = "actionType", TYPE = "type";
	
	@Override
	public BasicAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		
		BasicAction action = clazz(obj.get(TYPE), Action.class, context).get();
		action.setIndex(integer(obj.get(INDEX), BasicAction.UNSET_INDEX));
		action.setClickType(clazz(obj.get(CLICK_TYPE), ClickType.class, context));
		action.setSlotActionType(clazz(obj.get(ACTION_TYPE), SlotActionType.class, context));

		return action.fromJson(obj, context);
	}

	@Override
	public JsonElement serialize(BasicAction src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.add(TYPE, context.serialize(src.getType()));
		obj.addProperty(INDEX, src.getIndex());
		obj.add(CLICK_TYPE, context.serialize(src.getClickType()));
		obj.add(ACTION_TYPE, context.serialize(src.getSlotActionType()));
		return src.toJson(obj, context);
	}
}