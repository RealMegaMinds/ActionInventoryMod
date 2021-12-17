package megaminds.actioninventory.serialization;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.callbacks.click.Action;
import megaminds.actioninventory.callbacks.click.BasicAction;
import net.minecraft.screen.slot.SlotActionType;

public class BasicActionSerializer implements JsonDeserializer<BasicAction> {
	private static final String INDEX = "index", CLICK_TYPE = "clickType", ACTION_TYPE = "actionType", TYPE = "type";
	
	@Override
	public BasicAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		
		if (!obj.has(TYPE)) throw new JsonParseException("BasicActions must have a type");
		BasicAction action = context.<Action>deserialize(obj.get(TYPE), Action.class).get();
		
		action.setIndex(obj.has(INDEX) ? obj.get(INDEX).getAsInt() : BasicAction.UNSET_INDEX);
		action.setClickType(obj.has(CLICK_TYPE) ? context.deserialize(obj.get(CLICK_TYPE), ClickType.class) : null);
		action.setSlotActionType(obj.has(ACTION_TYPE) ? context.deserialize(obj.get(ACTION_TYPE), SlotActionType.class) : null);

		return action.fromJson(obj, context);
	}
}