package megaminds.actioninventory.util;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

public class ItemStackish {
	//TODO: Defaults?
	public Item item;
	public int count, damage;
	public Text customName, lore[];	//displayMatches
	public LeveledEnchantment[] enchantments;	//enchantmentsMatch
	public NbtCompound nbt;
	public TooltipSection[] hideFlags;	//displayMatches
	public int color;	//displayMatches
	public Attribute[] attributes;	//attributesMatch
	
	public boolean matches(ItemStack stack) {
		return stack!=null
				&& Helper.nullOrEquals(item, stack::getItem)
				&& Helper.negativeOrEquals(count, stack::getCount)
				&& Helper.negativeOrEquals(damage, stack::getDamage)
				&& displayMatches(stack)
				&& Helper.nullOrEquals(customName, stack::getName)
				&& enchantmentsMatch(stack)
				&& Helper.compareNbt(nbt, stack.writeNbt(new NbtCompound()))
				&& attributesMatch(stack);
	}
	
	private boolean enchantmentsMatch(ItemStack stack) {
		return false;
	}

	private boolean attributesMatch(ItemStack stack) {
		return false;
	}

	public ItemStack toStack() {
		ItemStack s = new ItemStack(Helper.get(item, Items.AIR), count>0 ? count : 1);
		//TODO implement
		return null;
	}
	
	public boolean displayMatches(ItemStack stack) {
		NbtCompound c = stack.getSubNbt(ItemStack.DISPLAY_KEY);
		if (c==null && (customName!=null || lore!=null || color>=0 || hideFlags!=null)) {
			return false;
		}	//else -> c != null

		if (color>=0 && (!c.contains(ItemStack.COLOR_KEY, NbtType.NUMBER) || color!=c.getInt(ItemStack.COLOR_KEY))) {
			return false;
		}
		
		NbtList list;
		if (lore!=null && (!c.contains(ItemStack.LORE_KEY, NbtType.LIST) || (list=c.getList(ItemStack.LORE_KEY, color))==null)) {
			return false;
		} else {
			
		}
		return true;
	}
	
	public class Attribute {
		public EntityAttribute attribute;
		public Operation operation;
		public double value;
		public String name;
		public EquipmentSlot slot;
		
		public void apply(ItemStack stack) {
			stack.addAttributeModifier(attribute, new EntityAttributeModifier(name, value, operation), slot);
		}
	}
	
	public class LeveledEnchantment {
		public Enchantment enchantment;
		public int level;
		
		public void apply(ItemStack stack) {
			stack.addEnchantment(enchantment, level);
		}
	}
}