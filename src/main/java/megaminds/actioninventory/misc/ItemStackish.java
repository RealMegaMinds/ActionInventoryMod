package megaminds.actioninventory.misc;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.serialization.wrappers.Validated;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtNull;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemStackish {
	public static final ItemStackish MATCH_ALL = new ItemStackish() {
		@Override public boolean specifiedEquals(ItemStack s) {return true;}
		@Override public boolean specifiedEquals(ItemStackish s) {return true;}
	};
	public static final ItemStackish MATCH_NONE = new ItemStackish() {
		@Override public boolean specifiedEquals(ItemStack s) {return false;}
		@Override public boolean specifiedEquals(ItemStackish s) {return false;}
	};
	
	private static final String HIDE_FLAG_KEY = "HideFlags";
	private static final String ATTRIBUTE_KEY = "AttributeModifiers";
	
	private Item item;
	private Integer count;
	private Integer damage; 
	private Optional<NbtCompound> customNbt;
	private Optional<Text> customName;	//displayMatches
	private Text[] lore;	//displayMatches
	private Optional<Integer> color;//color in display
	private Map<Enchantment, Integer> enchantments;	//enchantmentsMatch
	private EnumSet<TooltipSection> hideFlags;	//displayMatches
	private Set<AttributeValues> attributes;	//attributesMatch
	
	public ItemStackish(ItemStack i) {
		if (ItemStack.EMPTY.equals(i)) return;
		
		item = i.getItem();
		count = i.getCount();
		damage = i.getDamage();
		setNbtDefaults();
		if (i.hasNbt()) {
			NbtCompound nbt = i.getNbt().copy();
			customNbt = Optional.of(nbt);
			if (nbt.contains(ItemStack.DISPLAY_KEY)) {
				NbtCompound display = nbt.getCompound(ItemStack.DISPLAY_KEY);
				nameFrom(i);
				loreFrom(display);
				colorFrom(display);
			}
			enchantments = EnchantmentHelper.get(i);
			flagsFrom(nbt);
			attributesFrom(nbt);
		}
	}
	
	public ItemStack toStack() {
		ItemStack s = new ItemStack(Objects.requireNonNullElse(item, Items.AIR));
		if (customNbt!=null) s.setNbt(customNbt.orElse(null));
		if (count!=null) s.setCount(count);
		if (damage!=null) s.setDamage(damage);
		if (customName!=null) s.setCustomName(customName.orElse(null));
		if (lore!=null) addLore(s);
		if (color!=null) addColor(s);
		if (enchantments!=null) EnchantmentHelper.set(enchantments, s);
		if (hideFlags!=null) hideFlags.forEach(s::addHideFlag);
		if (attributes!=null) attributes.forEach(a->a.apply(s));
		return s;
	}
	
	public void setNbtDefaults() {
		customNbt = Optional.empty();
		color = Optional.empty();
		customName = Optional.empty();
		lore = new Text[0];
		enchantments = Map.of();
		hideFlags = EnumSet.noneOf(TooltipSection.class);
		attributes = Set.of();
	}
	
	private void nameFrom(ItemStack s) {
		if (s.hasCustomName()) {
			customName = Optional.of(s.getName());
			s.getSubNbt(ItemStack.DISPLAY_KEY).remove(ItemStack.NAME_KEY);
		}
	}
	
	private void colorFrom(NbtCompound display) {
		if (display.contains(ItemStack.COLOR_KEY)) {
			color = Optional.of(display.getInt(ItemStack.COLOR_KEY));
			display.remove(ItemStack.COLOR_KEY);
		}
	}
	
	private void flagsFrom(NbtCompound nbt) {
		if (nbt.contains(HIDE_FLAG_KEY)) {
			final int flags = nbt.getInt(HIDE_FLAG_KEY);
			hideFlags = EnumSet.allOf(TooltipSection.class);
			hideFlags.removeIf(s -> (flags&s.getFlag()) != 0);
			nbt.remove(HIDE_FLAG_KEY);
		}
	}
	
	private void attributesFrom(NbtCompound nbt) {
		if (nbt.contains(ATTRIBUTE_KEY)) {
			attributes = nbt.getList(ATTRIBUTE_KEY, NbtType.COMPOUND).stream()
					.map(NbtCompound.class::cast)
					.map(AttributeValues::new)
					.collect(HashSet::new, Set::add, Set::addAll);
			nbt.remove(ATTRIBUTE_KEY);
		}
	}
	
	private void loreFrom(NbtCompound display) {
		if (display.contains(ItemStack.LORE_KEY)) {
			lore = display.getList(ItemStack.LORE_KEY, NbtType.STRING).stream()
					.map(l->l==null||l==NbtNull.INSTANCE?"":l.asString())
					.map(Text.Serializer::fromJson)
					.toArray(Text[]::new);
			display.remove(ItemStack.LORE_KEY);
		}
	}

	private void addLore(ItemStack s) {
		NbtList list = Arrays.stream(lore)
				.map(l->l!=null?l:LiteralText.EMPTY)
				.map(Text.Serializer::toJson)
				.map(NbtString::of)
				.collect(NbtList::new, NbtList::add, NbtList::addAll);
		s.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, list);
	}
	
	private void addColor(ItemStack s) {
		NbtCompound el;
		if ((el=s.getNbt())!=null && el.contains(ItemStack.DISPLAY_KEY)) {
			if (color.isEmpty()) {
				el.getCompound(ItemStack.DISPLAY_KEY).remove(ItemStack.COLOR_KEY);
			} else {
				el.putInt(ItemStack.DISPLAY_KEY, color.get());
			}
		}
	}

	/**
	 * Checks contained elements for equality.<br>
	 * Unspecified elements are not checked.<br>
	 * o1.equals(o2) doesn't mean o2.equals(o1)
	 */
	public boolean specifiedEquals(ItemStackish i) {
		if (this==i) return true;
		if (i==null) return false;
		
		return nullOrEquals(item, i.item)
				&& nullOrEquals(count, i.count)
				&& nullOrEquals(damage, i.damage)
				&& nullOrEquals(color, i.color)
				&& (customName==null || i.customName!=null&&textEqual(customName.orElse(null), i.customName.orElse(null)))
				&& (lore==null || i.lore!=null&&loreEquals(i.lore))
				&& nullOrEquals(enchantments, i.enchantments)
				&& (customNbt==null || i.customNbt!=null&&NbtHelper.matches(customNbt.orElse(null), i.customNbt.orElse(null), true))	//NOSONAR Minecraft uses this other places so I think this is fine.
				&& nullOrEquals(hideFlags, i.hideFlags)
				&& nullOrEquals(attributes, i.attributes);
	}
	
	private static <E> boolean nullOrEquals(E e, E other) {
		return e==null || Objects.equals(e, other);
	}
	
	public boolean specifiedEquals(ItemStack s) {
		return this.specifiedEquals(new ItemStackish(s));
	}
	
	private static boolean textEqual(Text t1, Text t2) {
		return Objects.equals(t1, t2) || 
				t1!=null && t2!=null && t1.getString().equals(t2.getString());
	}

	private boolean loreEquals(Text[] lore2) {
		int l = lore.length;
		for (int i=0; i<l; i++) {
			if (!textEqual(lore[i], lore2[i])) return false;
		}
		return true;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public class AttributeValues implements Validated {
		private EntityAttribute attribute;
		private Operation operation;
		private double value;
		private String name;
		private UUID uuid;
		private EquipmentSlot slot;
		
		@Override
		public void validate() {
			Validated.validate(attribute!=null, "Attribute modifiers need attribute to be non-null");
			Validated.validate(name!=null, "Attribute modifiers need name to be non-null");
			Validated.validate(operation!=null, "Attribute modifiers need operation to be non-null");
		}
		public AttributeValues(NbtCompound c) {
			EntityAttributeModifier mod = EntityAttributeModifier.fromNbt(c);
			attribute = Registry.ATTRIBUTE.get(new Identifier(c.getString("AttributeName")));
			operation = mod.getOperation();
			value = mod.getValue();
			name = mod.getName();
			uuid = mod.getId();
			slot = EquipmentSlot.valueOf(c.getString("Slot"));			
		}

		public void apply(ItemStack stack) {
			EntityAttributeModifier mod = uuid==null ? new EntityAttributeModifier(name, value, operation) : new EntityAttributeModifier(uuid, name, value, operation);
			stack.addAttributeModifier(attribute, mod, slot);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof AttributeValues other)) return false;
			return Objects.equals(attribute, other.attribute)
					&& Objects.equals(name, other.name)
					&& Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value)
					&& operation == other.operation
					&& slot == other.slot
					&& Objects.equals(uuid, other.uuid);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ItemStackish.this.hashCode();
			result = prime * result + Objects.hash(attribute, name, operation, slot, uuid, value);
			return result;
		}

	}
}