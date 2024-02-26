package megaminds.actioninventory.misc;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import megaminds.actioninventory.serialization.wrappers.Validated;
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
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
	private Set<TooltipSection> hideFlags;	//displayMatches
	private Set<AttributeValues> attributes;	//attributesMatch

	public ItemStackish() {}

	@SuppressWarnings("java:S107")
	public ItemStackish(Item item, Integer count, Integer damage, Optional<NbtCompound> customNbt, Optional<Text> customName, Text[] lore, Optional<Integer> color, Map<Enchantment, Integer> enchantments, Set<TooltipSection> hideFlags, Set<AttributeValues> attributes) {
		this.item = item;
		this.count = count;
		this.damage = damage;
		this.customNbt = customNbt;
		this.customName = customName;
		this.lore = lore;
		this.color = color;
		this.enchantments = enchantments;
		this.hideFlags = hideFlags;
		this.attributes = attributes;
	}

	public ItemStackish(ItemStack i) {
		if (ItemStack.EMPTY.equals(i)) return;

		item = i.getItem();
		count = i.getCount();
		damage = i.getDamage();
		setNbtDefaults();
		if (i.hasNbt()) {
			NbtCompound nbt = i.getNbt().copy();
			customNbt = Optional.of(nbt);
			//All of the found values are removed from the NbtCompound so only custom info stays. ** The ItemStack itself should NOT be modified. **
			if (nbt.contains(ItemStack.DISPLAY_KEY)) {
				NbtCompound display = nbt.getCompound(ItemStack.DISPLAY_KEY);
				nameFrom(display);
				loreFrom(display);
				colorFrom(display);
			}
			enchantments = EnchantmentHelper.get(i);
			flagsFrom(nbt);
			attributesFrom(nbt);
		}
	}

	@SuppressWarnings("java:S2789")
	public ItemStack toStack() {
		ItemStack s = new ItemStack(Objects.requireNonNullElse(item, Items.AIR));
		if (customNbt!=null) {
			if (customNbt.isEmpty()) {
				s.setNbt(null);
			} else {
				s.setNbt(new NbtCompound());
				s.getNbt().copyFrom(customNbt.orElseThrow());
			}
		}
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

	private void nameFrom(NbtCompound display) {
		if (display.contains(ItemStack.NAME_KEY)) {
			customName = Optional.of(Text.Serialization.fromJson(display.getString(ItemStack.NAME_KEY)));
			display.remove(ItemStack.NAME_KEY);
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
			attributes = nbt.getList(ATTRIBUTE_KEY, NbtElement.COMPOUND_TYPE).stream()
					.map(NbtCompound.class::cast)
					.map(AttributeValues::new)
					.collect(HashSet::new, Set::add, Set::addAll);
			nbt.remove(ATTRIBUTE_KEY);
		}
	}

	private void loreFrom(NbtCompound display) {
		if (display.contains(ItemStack.LORE_KEY)) {
			lore = display.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE).stream()
					.map(l->l==null||l==NbtEnd.INSTANCE?"":l.asString())
					.map(Text.Serialization::fromJson)
					.toArray(Text[]::new);
			display.remove(ItemStack.LORE_KEY);
		}
	}

	public static List<MutableText> loreFrom(ItemStack stack) {
		var display = stack.getSubNbt(ItemStack.DISPLAY_KEY);
		if (display !=null && display.contains(ItemStack.LORE_KEY)) {
			return display.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE).stream()
					.map(l->l==null||l==NbtEnd.INSTANCE?"":l.asString())
					.map(Text.Serialization::fromJson)
					.toList();
		}
		return Collections.emptyList();
	}

	private void addLore(ItemStack s) {
		NbtList list = Arrays.stream(lore)
				.map(l->l!=null?l:Text.empty())
				.map(Text.Serialization::toJsonString)
				.map(NbtString::of)
				.collect(NbtList::new, NbtList::add, NbtList::addAll);
		s.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, list);
	}

	public static void setLore(ItemStack stack, List<Text> lore) {
		var list = lore.stream()
				.map(l->l!=null?l:Text.empty())
				.map(Text.Serialization::toJsonString)
				.map(NbtString::of)
				.collect(NbtList::new, NbtList::add, NbtList::addAll);
		stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, list);
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
	@SuppressWarnings("java:S2789")
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

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getDamage() {
		return damage;
	}

	public void setDamage(Integer damage) {
		this.damage = damage;
	}

	public Optional<NbtCompound> getCustomNbt() {
		return customNbt;
	}

	public void setCustomNbt(Optional<NbtCompound> customNbt) {
		this.customNbt = customNbt;
	}

	public Optional<Text> getCustomName() {
		return customName;
	}

	public void setCustomName(Optional<Text> customName) {
		this.customName = customName;
	}

	public Text[] getLore() {
		return lore;
	}

	public void setLore(Text[] lore) {
		this.lore = lore;
	}

	public Optional<Integer> getColor() {
		return color;
	}

	public void setColor(Optional<Integer> color) {
		this.color = color;
	}

	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	public void setEnchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments = enchantments;
	}

	public Set<TooltipSection> getHideFlags() {
		return hideFlags;
	}

	public void setHideFlags(Set<TooltipSection> hideFlags) {
		this.hideFlags = hideFlags;
	}

	public Set<AttributeValues> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<AttributeValues> attributes) {
		this.attributes = attributes;
	}

	public class AttributeValues implements Validated {
		private EntityAttribute attribute;
		private Operation operation;
		private double value;
		private String name;
		private UUID uuid;
		private EquipmentSlot slot;

		public AttributeValues() {}

		public AttributeValues(EntityAttribute attribute, Operation operation, double value, String name, UUID uuid, EquipmentSlot slot) {
			this.attribute = attribute;
			this.operation = operation;
			this.value = value;
			this.name = name;
			this.uuid = uuid;
			this.slot = slot;
		}

		@Override
		public void validate() {
			Validated.validate(attribute!=null, "Attribute modifiers need attribute to be non-null");
			Validated.validate(name!=null, "Attribute modifiers need name to be non-null");
			Validated.validate(operation!=null, "Attribute modifiers need operation to be non-null");
		}

		public AttributeValues(NbtCompound c) {
			EntityAttributeModifier mod = EntityAttributeModifier.fromNbt(c);
			attribute = Registries.ATTRIBUTE.get(new Identifier(c.getString("AttributeName")));
			operation = mod.getOperation();
			value = mod.getValue();
			name = c.getString("Name");
			uuid = mod.getId();
			slot = EquipmentSlot.valueOf(c.getString("Slot"));			
		}

		public void apply(ItemStack stack) {
			EntityAttributeModifier mod = uuid==null ? new EntityAttributeModifier(name, value, operation) : new EntityAttributeModifier(uuid, name, value, operation);
			stack.addAttributeModifier(attribute, mod, slot);
		}

		public EntityAttribute getAttribute() {
			return attribute;
		}

		public void setAttribute(EntityAttribute attribute) {
			this.attribute = attribute;
		}

		public Operation getOperation() {
			return operation;
		}

		public void setOperation(Operation operation) {
			this.operation = operation;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public UUID getUuid() {
			return uuid;
		}

		public void setUuid(UUID uuid) {
			this.uuid = uuid;
		}

		public EquipmentSlot getSlot() {
			return slot;
		}

		public void setSlot(EquipmentSlot slot) {
			this.slot = slot;
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