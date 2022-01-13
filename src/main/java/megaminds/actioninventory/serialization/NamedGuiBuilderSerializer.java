package megaminds.actioninventory.serialization;

import static megaminds.actioninventory.util.JsonHelper.*;
import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import megaminds.actioninventory.gui.AccessableAnimatedGuiElement;
import megaminds.actioninventory.gui.AccessableGuiElement;
import megaminds.actioninventory.gui.NamedGuiBuilder;
import megaminds.actioninventory.gui.SlotFunction;
import megaminds.actioninventory.util.JsonHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NamedGuiBuilderSerializer implements JsonDeserializer<NamedGuiBuilder>, JsonSerializer<NamedGuiBuilder> {
	//builder fields
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String INCLUDE_PLAYER = "includePlayerSlots";
	private static final String TITLE = "title";
	private static final String LOCK_PLAYER = "lockPlayerSlots";
	private static final String SLOTS = "slots";
	//all slot fields
	private static final String SLOT_TYPE = "type";
	private static final String INDEX = "index";
	private enum SlotType {
		NORMAL {
		@Override
		void add(NamedGuiBuilder b, JsonElement s, int i, JsonDeserializationContext c) {
			b.setSlot(i, c.<AccessableGuiElement>deserialize(s, AccessableGuiElement.class));
		}
	}, ANIMATED {
		@Override
		void add(NamedGuiBuilder b, JsonElement s, int i, JsonDeserializationContext c) {
			b.setSlot(i, c.<AccessableAnimatedGuiElement>deserialize(s, AccessableAnimatedGuiElement.class));
		}
	}, REDIRECT {
		@Override
		void add(NamedGuiBuilder b, JsonElement s, int i, JsonDeserializationContext c) {
			b.setSlot(i, c.<SlotFunction>deserialize(s, SlotFunction.class));
		}
	}; abstract void add(NamedGuiBuilder b, JsonElement s, int i, JsonDeserializationContext c);}


	@Override
	public NamedGuiBuilder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();

		//Make the builder
		String type = orError(obj.get(TYPE), JsonElement::getAsString, "NamedGuis must have a type");		
		boolean includePlayer = bool(obj.get(INCLUDE_PLAYER));
		NamedGuiBuilder builder = new NamedGuiBuilder(Registry.SCREEN_HANDLER.get(new Identifier(type)), includePlayer);

		//Get the builder's name
		Identifier name = orError(obj.get(NAME), JsonHelper::identifier, "NamedGuis must have a name");
		builder.setName(name);

		//Set builder's values
		getDo(obj.get(TITLE), Text.Serializer::fromJson, builder::setTitle);
		getDo(obj.get(LOCK_PLAYER), JsonElement::getAsBoolean, builder::setLockPlayerInventory);

		//Set builder's slots by iterating through all given values
		getDo(obj.get(SLOTS), JsonElement::getAsJsonArray, slots->{
			for (JsonElement el : slots) {
				JsonObject slot = el.getAsJsonObject();

				//Get the correct index, or throw error
				int index = integer(slot.get(INDEX), builder::getFirstEmptySlot);
				if (index >= builder.getSize()) throw new JsonParseException("Slot index must be less than the defined size of: "+builder.getSize());
				if (index < 0) throw new JsonParseException("No more empty slots. Slot index must be defined.");


				//Deserialize based on the slot's type
				clazz(slot.get(SLOT_TYPE), SlotType.class, context, SlotType.NORMAL).add(builder, slot, index, context);
			}
		});

		//return the finished builder
		return builder;
	}

	/**
	 * This is unable to completely serialize a NamedGuiBuilder and notes where it can't.
	 */
	@Override
	public JsonElement serialize(NamedGuiBuilder src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.add(NAME, context.serialize(src.getName()));
		obj.addProperty(TYPE, Registry.SCREEN_HANDLER.getId(src.getType()).toString());
		obj.add(TITLE, context.serialize(src.getTitle()));
		obj.addProperty(INCLUDE_PLAYER, src.isIncludingPlayer());
		obj.addProperty(LOCK_PLAYER, src.getLockPlayerInventory());
		int size = src.getSize();
		JsonArray slots = new JsonArray(size);
		for (int i = 0; i < size; i++) {
			GuiElementInterface g;
			SlotFunction func;
			if ((g=src.getSlot(i))!=null) {
				if (g!=GuiElement.EMPTY) slots.add(context.serialize(g));
			} else if ((func=src.getSlotFunc(i))!=null) {
				slots.add(context.serialize(func));
			}
		}
		obj.add(SLOTS, slots);
		return obj;
	}
}