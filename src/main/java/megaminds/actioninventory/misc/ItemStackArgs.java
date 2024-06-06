package megaminds.actioninventory.misc;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class ItemStackArgs {
	public Item item;
	public int count;
	public int damage; 
	public NbtCompound customNbt;
	public Text customName;
	public Text[] lore;
	public int color;
	public Map<Enchantment, Integer> enchantments;
	public EnumSet<TooltipSection> hideFlags;
	public Set<AttributeValues> attributes;
	
	public ItemStackArgs() {
		count = -1;
		damage = -1;
		color = -1;
	}
	
	public void copyFrom(ItemStack stack) {
		
	}
	
	public ItemStackArgs copy() {
		return null;
	}
	
	public static class AttributeValues {
		public EntityAttribute attribute;
		public Operation operation;
		public double value;
		public String name;
		public UUID uuid;
		public EquipmentSlot slot;
		
		public AttributeValues() {
			value = Double.NaN;
		}
	}
}