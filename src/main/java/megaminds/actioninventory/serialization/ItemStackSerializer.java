package megaminds.actioninventory.serialization;

import java.lang.reflect.Type;

import org.spongepowered.include.com.google.gson.JsonSyntaxException;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemStackSerializer implements JsonDeserializer<ItemStack> {
	//most useful
	private static final String ITEM = "item", COUNT = "count", NAME = "name", LORE = "lore", DAMAGE = "damage", ENCHANMENTS = "enchantments";
	//others
	private static final String NBT = "nbt", HIDE_FLAGS = "hideFlags", COLOR = "color", ATTRIBUTES = "attributes", REPAIR_COST = "cost";
	//enchantment
	private static final String LEVEL = "level", ENCHANTMENT = "enchantment";
	//attribute
	private static final String ATTRIB_NAME = "name", VALUE = "value", OPERATION = "operation", TYPE = "type", SLOT = "slot";
	
	@Override
	public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		
		Item item = obj.has(ITEM) ? Registry.ITEM.get(new Identifier(obj.get(ITEM).getAsString())) : Items.AIR;
		ItemStack stack = new ItemStack(item);
		if (obj.has(COUNT)) stack.setCount(obj.get(COUNT).getAsInt());
		if (obj.has(NAME)) stack.setCustomName(Text.Serializer.fromJson(obj.get(NAME)));
		if (obj.has(DAMAGE)) stack.setDamage(obj.get(DAMAGE).getAsInt());
		if (obj.has(LORE)) {
			NbtList loreNbt = new NbtList();
			if (obj.get(LORE) instanceof JsonArray lore) {
				for (JsonElement line : lore) {
					addLoreLine(loreNbt, line);
				}
			} else {
				addLoreLine(loreNbt, obj.get(LORE));
			}
			stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, loreNbt);
		}
		
		if (obj.has(ENCHANMENTS)) {
			if (obj.get(ENCHANMENTS) instanceof JsonArray enchantments) {
				for (JsonElement el : enchantments) {
					addEnchantment(stack, el.getAsJsonObject());
				}
			} else {
				addEnchantment(stack, obj.get(ENCHANMENTS).getAsJsonObject());
			}
		}
		
		if (obj.has(NBT)) {
			try {
				stack.getOrCreateNbt().copyFrom(StringNbtReader.parse(obj.get(NBT).getAsString()));
			} catch (CommandSyntaxException e) {
				throw new JsonSyntaxException("Failed to read nbt.", e);
			}
		}
		
		if (obj.has(HIDE_FLAGS)) {
			if (obj.get(HIDE_FLAGS) instanceof JsonArray flags) {
				for (JsonElement flag : flags) {
					addHideFlag(stack, context, flag);
				}
			} else {
				addHideFlag(stack, context, obj.get(HIDE_FLAGS));
			}
		}
		
		if (obj.has(COLOR)) {
			JsonPrimitive col = obj.get(COLOR).getAsJsonPrimitive();
			int color;
			if (col.isNumber()) {
				color = col.getAsInt();
			} else {
				color = context.<Formatting>deserialize(col, Formatting.class).getColorValue();
			}
			stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.COLOR_KEY, NbtInt.of(color));
		}
		
		if (obj.has(ATTRIBUTES)) {
			if (obj.get(ATTRIBUTES) instanceof JsonArray attributes) {
				for (JsonElement attribute : attributes) {
					addAttribute(stack, context, attribute.getAsJsonObject());
				}
			} else {
				addAttribute(stack, context, obj.get(ATTRIBUTES).getAsJsonObject());
			}
		}
		
		if (obj.has(REPAIR_COST)) stack.setRepairCost(obj.get(REPAIR_COST).getAsInt());

		return stack;
	}
	
	private static void addAttribute(ItemStack stack, JsonDeserializationContext context, JsonObject attribute) {
		EntityAttribute attrib = attribute.has(TYPE) ? Registry.ATTRIBUTE.get(new Identifier(attribute.get(TYPE).getAsString())) : EntityAttributes.GENERIC_MAX_HEALTH;
		Operation operation = attribute.has(OPERATION) ? context.deserialize(attribute.get(OPERATION), Operation.class) : Operation.ADDITION;
		double value = attribute.has(VALUE) ? attribute.get(VALUE).getAsDouble() : 0;
		String name = attribute.has(ATTRIB_NAME) ? attribute.get(ATTRIB_NAME).getAsString() : "";
		EquipmentSlot slot = attribute.has(SLOT) ? context.deserialize(attribute.get(SLOT), EquipmentSlot.class) : null;
		stack.addAttributeModifier(attrib, new EntityAttributeModifier(name, value, operation), slot);
	}
	
	private static void addHideFlag(ItemStack stack, JsonDeserializationContext context, JsonElement flag) {
		stack.addHideFlag(context.deserialize(flag, TooltipSection.class));
	}
	
	private static void addLoreLine(NbtList loreList, JsonElement loreLine) {
		loreList.add(NbtString.of(loreLine.toString()));
	}
	
	private static void addEnchantment(ItemStack stack, JsonObject enchantment) {
		int level = enchantment.has(LEVEL) ? enchantment.get(LEVEL).getAsInt() : 1;
		Enchantment ench = enchantment.has(ENCHANTMENT) ? Registry.ENCHANTMENT.get(new Identifier(enchantment.get(ENCHANTMENT).getAsString())) : null;
		stack.addEnchantment(ench, level);
	}
}