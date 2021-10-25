package megaminds.testmod.inventory;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.util.UUIDTypeAdapter;

import megaminds.testmod.inventory.actions.Action;
import megaminds.testmod.inventory.actions.ActionSerializer;
import megaminds.testmod.inventory.openers.Opener;
import megaminds.testmod.inventory.openers.OpenerSerializer;
import megaminds.testmod.inventory.requirements.Requirement;
import megaminds.testmod.inventory.requirements.RequirementSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;

public class ActionJsonHelper {
	public static final Gson GSON;
	
	public static String toJson(Object o) {
		return GSON.toJson(o);
	}
	
	public static ActionInventory fromJson(String json) {
		return GSON.fromJson(json, ActionInventory.class);
	}
	
	public static class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
		@Override
		public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			try {
				return ItemStack.fromNbt(StringNbtReader.parse(json.getAsString()));
			} catch (CommandSyntaxException e) {
				throw new JsonParseException(e);
			}
		}

		@Override
		public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(new StringNbtWriter().apply(src.writeNbt(new NbtCompound())));
		}
	}
	
	static {
		GSON = new GsonBuilder()
				.setPrettyPrinting()
				.disableHtmlEscaping()
				.registerTypeAdapter(Action.class, new ActionSerializer())
				.registerTypeAdapter(Requirement.class, new RequirementSerializer())
				.registerTypeAdapter(Opener.class, new OpenerSerializer())
				.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
				.registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
				.registerTypeAdapter(Identifier.class, new Identifier.Serializer())
				.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
				.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
				.registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
				.create();
	}
}