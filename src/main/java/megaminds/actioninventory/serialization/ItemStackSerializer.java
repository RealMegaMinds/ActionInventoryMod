package megaminds.actioninventory.serialization;

import static megaminds.actioninventory.util.JsonHelper.getOrDefault;
import static megaminds.actioninventory.util.JsonHelper.getDo;
import static megaminds.actioninventory.util.JsonHelper.getDoForEach;

import java.lang.reflect.Type;
import java.util.Arrays;
import org.spongepowered.include.com.google.gson.JsonSyntaxException;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import megaminds.actioninventory.util.Helper;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemStackSerializer implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {
	//most useful
	private static final String ITEM = "item", COUNT = "count", NAME = "name", LORE = "lore", DAMAGE = "damage", ENCHANMENTS = "enchantments";
	//others
	private static final String NBT = "nbt", HIDE_FLAGS = "hideFlags", COLOR = "color", ATTRIBUTES = "attributes";
	//enchantment
	private static final String LEVEL = "level", ENCHANTMENT = "enchantment";
	//attribute
	private static final String ATTRIB_NAME = "name", VALUE = "value", OPERATION = "operation", TYPE = "type", SLOT = "slot";
		
	@Override
	public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		
		ItemStack stack = new ItemStack(Registry.ITEM.get(new Identifier(getOrDefault(obj.get(ITEM), JsonElement::getAsString, "air"))));
		getDo(obj.get(COUNT), JsonElement::getAsInt, stack::setCount);
		getDo(obj.get(NAME), Text.Serializer::fromJson, stack::setCustomName);
		getDo(obj.get(DAMAGE), JsonElement::getAsInt, stack::setDamage);
		
		NbtList loreNbt = new NbtList();
		getDoForEach(obj.get(LORE), JsonElement::toString, s->loreNbt.add(NbtString.of(s)));
		stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, loreNbt);
		
		getDoForEach(obj.get(ENCHANMENTS), JsonElement::getAsJsonObject,  o->{
			Enchantment ench = Registry.ENCHANTMENT.get(new Identifier(getOrDefault(o.get(ENCHANTMENT), JsonElement::getAsString, null)));
			int lvl = getOrDefault(o.get(LEVEL), JsonElement::getAsInt, 1);
			stack.addEnchantment(ench, lvl);
		});

		getDoForEach(obj.get(HIDE_FLAGS), f->context.deserialize(f, TooltipSection.class), stack::addHideFlag);

		getDo(obj.get(COLOR), JsonElement::getAsJsonPrimitive, col->{
			int color = col.isNumber() ? col.getAsInt() : context.<Formatting>deserialize(col, Formatting.class).getColorValue();
			stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.COLOR_KEY, NbtInt.of(color));
		});
		
		getDoForEach(obj.get(ATTRIBUTES), JsonElement::getAsJsonObject, o->addAttribute(o, context, stack));
		
		getDo(obj.get(NBT), JsonElement::getAsString, s->{
			try {
				stack.getOrCreateNbt().copyFrom(StringNbtReader.parse(s));
			} catch (CommandSyntaxException e) {
				throw new JsonSyntaxException("Failed to read nbt.", e);
			}
		});
		
		return stack;
	}
	
	private static void addAttribute(JsonObject attribute, JsonDeserializationContext context, ItemStack stack) {
		EntityAttribute attrib = Registry.ATTRIBUTE.get(new Identifier(getOrDefault(attribute.get(TYPE), JsonElement::getAsString, "generic.attack_damage")));
		Operation operation = getOrDefault(attribute.get(OPERATION), Operation.class, context::deserialize, Operation.ADDITION);
		double value = getOrDefault(attribute.get(VALUE), JsonElement::getAsDouble, 0.0);
		String name = getOrDefault(attribute.get(ATTRIB_NAME), JsonElement::getAsString, "");
		EquipmentSlot slot = getOrDefault(attribute.get(SLOT), EquipmentSlot.class, context::deserialize, EquipmentSlot.MAINHAND);
		stack.addAttributeModifier(attrib, new EntityAttributeModifier(name, value, operation), slot);
	}
	
	private static JsonObject attributeToObj(EntityAttributeModifier modifier, EntityAttribute attrib, EquipmentSlot slot, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.addProperty(TYPE, Registry.ATTRIBUTE.getId(attrib).toString());
		obj.add(OPERATION, context.serialize(modifier.getOperation()));
		obj.addProperty(VALUE, modifier.getValue());
		obj.addProperty(ATTRIB_NAME, modifier.getName());
		obj.add(SLOT, context.serialize(slot));
		return obj;
	}
	
	@Override
	public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.addProperty(ITEM, Registry.ITEM.getId(src.getItem()).toString());
		obj.addProperty(COUNT, src.getCount());
		if (src.hasCustomName()) obj.add(NAME, Text.Serializer.toJsonTree(src.getName()));
		Helper.ifDo(src.getSubNbt(ItemStack.DISPLAY_KEY), d-> {
			JsonArray arr = new JsonArray();
			Helper.getDoForEach(d.getList(ItemStack.LORE_KEY, NbtType.STRING), NbtString.class::cast, n->arr.add(JsonParser.parseString(n.asString())));
			obj.add(LORE, arr);
			
			Helper.ifDo(d.getInt(ItemStack.COLOR_KEY), i->obj.addProperty(COLOR, i));
		});
		obj.addProperty(DAMAGE, src.getDamage());
		Helper.ifDo(src.getEnchantments(), l->{
			JsonArray arr = new JsonArray();
			Helper.getDoForEach(l, NbtCompound.class::cast, e->{
				JsonObject o = new JsonObject();
				o.addProperty(LEVEL, EnchantmentHelper.getLevelFromNbt(e));
				o.addProperty(ENCHANTMENT, EnchantmentHelper.getIdFromNbt(e).toString());
				arr.add(o);
			});
			obj.add(ENCHANMENTS, arr);
		});
		obj.addProperty(NBT, src.writeNbt(new NbtCompound()).asString());
		Helper.getDo(src.getNbt(), c->c.getInt("HideFlags"), f->{
			JsonArray arr = new JsonArray();
			Arrays.stream(TooltipSection.values()).filter(s->(f&s.getFlag()) == 0).map(context::serialize).forEach(arr::add);
			obj.add(HIDE_FLAGS, arr);
		});
		JsonArray arr = new JsonArray();
		Arrays.stream(EquipmentSlot.values()).forEach(s->src.getAttributeModifiers(s).forEach((k,v)->arr.add(attributeToObj(v, k, s, context))));
		if (!arr.isEmpty()) obj.add(ATTRIBUTES, arr);
		return obj;
	}
}