package megaminds.actioninventory.serialization;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import eu.pb4.sgui.api.elements.AnimatedGuiElement;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import megaminds.actioninventory.callbacks.click.BasicAction;
import megaminds.actioninventory.gui.NamedGuiBuilder;
import megaminds.actioninventory.util.GuiIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NamedGuiBuilderSerializer implements JsonDeserializer<NamedGuiBuilder> {
	//builder fields
	private static final String NAME = "name", TYPE = "type", INCLUDE_PLAYER = "includePlayerSlots", TITLE = "title", LOCK_PLAYER = "lockPlayerSlots", SLOTS = "slots";
	//all slot fields
	private static final String SLOT_TYPE = "type", INDEX = "index", CALLBACK = "callback", ITEMS = "items";
	//animated slot fields
	private static final String RANDOM = "isRandom", INTERVAL = "interval";
	//redirect fields
	private static final String REDIRECT_INDEX = "redirectIndex";
	//slot types
	private static final String NORMAL = "normal", REDIRECT = "redirect";
	
	@Override
	public NamedGuiBuilder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();

		//Make the builder
		if (!obj.has(TYPE)) throw new JsonParseException("NamedGuis must have a type");
		ScreenHandlerType<?> type = Registry.SCREEN_HANDLER.get(new Identifier(obj.get(TYPE).getAsString()));
		boolean includePlayer = obj.has(INCLUDE_PLAYER) ? obj.get(INCLUDE_PLAYER).getAsBoolean() : false;
		NamedGuiBuilder builder = new NamedGuiBuilder(type, includePlayer);

		//Get the builder's name
		if (!obj.has(NAME)) throw new JsonParseException("NamedGuis must have a name");
		builder.setName(obj.get(NAME).getAsString().replaceAll("\s", ""));

		//Set builder's values
		if (obj.has(TITLE)) builder.setTitle(Text.Serializer.fromJson(obj.get(TITLE)));
		if (obj.has(LOCK_PLAYER)) builder.setLockPlayerInventory(obj.get(LOCK_PLAYER).getAsBoolean());

		//Set builder's slots by iterating through all given values
		if (obj.has(SLOTS)) {
			JsonArray slots = obj.get(SLOTS).getAsJsonArray();
			for (JsonElement el : slots) {
				JsonObject slot = el.getAsJsonObject();
				
				//Get the correct index, or throw error
				int index = slot.has(INDEX) ? slot.get(INDEX).getAsInt() : builder.getFirstEmptySlot();
				if (index >= builder.getSize()) throw new JsonParseException("Slot index must be less than the defined size of: "+builder.getSize());
				if (index < 0) throw new JsonParseException("No more empty slots. Slot index must be defined.");
				
				//Get callback or empty
				ClickCallback callback = slot.has(CALLBACK) ? context.deserialize(slot.get(CALLBACK), BasicAction.class) : GuiElementInterface.EMPTY_CALLBACK;

				//Do rest based on the slot's type
				String slotType = slot.has(SLOT_TYPE) ? slot.get(SLOT_TYPE).getAsString() : NORMAL;
				switch (slotType) {
				case NORMAL -> {
					GuiElementInterface guiEl;
					if (slot.has(ITEMS)) {
						if (slot.get(ITEMS) instanceof JsonArray items) {	//multiple items, animated gui element
							//iterate through deserializing each item
							ItemStack[] stacks = new ItemStack[items.size()];
							for (int i = 0; i < items.size(); i++) {
								stacks[i] = context.deserialize(items.get(i), ItemStack.class);
							}
							
							//get interval and random
							int interval = slot.has(INTERVAL) ? slot.get(INTERVAL).getAsInt() : 1;
							boolean random = slot.has(RANDOM) ? slot.get(RANDOM).getAsBoolean() : false;
							
							//return the new element
							guiEl = new AnimatedGuiElement(stacks, interval, random, callback);
							
						} else {	//single item, simple gui element
							guiEl = new GuiElement(context.deserialize(slot.get(ITEMS), ItemStack.class), callback);
						}
						
					} else {	//no items, empty element
						guiEl = GuiElement.EMPTY;
					}

					//set the slot in the builder to the created gui element
					builder.setSlot(index, guiEl);
				}
				
				case REDIRECT -> {
					//set the redirect slot in the builder with deserialized slot
					int redirectIndex = slot.has(REDIRECT_INDEX) ? slot.get(REDIRECT_INDEX).getAsInt() : 0;
					builder.setSlotRedirect(index, getSlotFunc(context.deserialize(slot, GuiIdentifier.class), redirectIndex));
				}
				
				//no known type, throw exception
				default -> throw new JsonParseException("Unknown Slot Type: "+slotType);
				}
			}
		}
		//return the finished builder
		return builder;
	}
	
	public static Function<ServerPlayerEntity, Slot> getSlotFunc(GuiIdentifier id, int index) {
		return p -> {
			ServerPlayerEntity real = id.name==null ? p : p.getServer().getPlayerManager().getPlayer(UUID.fromString(id.name));
			return new Slot(id.isEnderChest ? real.getEnderChestInventory() : real.getInventory(), index, 0, 0);
		};
	}
}