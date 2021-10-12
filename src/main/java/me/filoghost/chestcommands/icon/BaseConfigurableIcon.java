/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.icon;

import me.filoghost.chestcommands.api.Icon;
import me.filoghost.chestcommands.placeholder.PlaceholderString;
import me.filoghost.chestcommands.placeholder.PlaceholderStringList;
import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.collection.CollectionUtils;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BaseConfigurableIcon implements Icon {

	private Item material;
	private int amount;
	private short durability;

	private NbtCompound nbtData;
	private PlaceholderString name;
	private PlaceholderStringList lore;
	private Map<Enchantment, Integer> enchantments;
	private DyeColor leatherColor;
	private PlaceholderString skullOwner;
	private Map<BannerPattern, DyeColor> bannerPatterns;
	private boolean placeholdersEnabled;

	private ItemStack cachedRendering; // Cache the rendered item when possible and if state hasn't changed

	public BaseConfigurableIcon(Item material) {
		this.material = material;
		this.amount = 1;
	}

	protected boolean shouldCacheRendering() {
		if (placeholdersEnabled && hasDynamicPlaceholders()) {
			return false;
		} else {
			return true;
		}
	}

	private boolean hasDynamicPlaceholders() {
		return (name != null && name.hasDynamicPlaceholders())
				|| (lore != null && lore.hasDynamicPlaceholders())
				|| (skullOwner != null && skullOwner.hasDynamicPlaceholders());
	}

	public void setMaterial(@NotNull Item material) {
		this.material = material;
		cachedRendering = null;
	}

	public @NotNull Item getMaterial() {
		return material;
	}

	public void setAmount(int amount) {
		Preconditions.checkArgument(amount > 0, "amount must be greater than 0");
		this.amount = Math.min(amount, 127);
		cachedRendering = null;
	}

	public int getAmount() {
		return amount;
	}

	public void setDurability(short durability) {
		Preconditions.checkArgument(durability >= 0, "durability must be 0 or greater");
		this.durability = durability;
		cachedRendering = null;
	}

	public short getDurability() {
		return durability;
	}

	public void setNBTData(@Nullable NbtCompound nbtData) {
		this.nbtData = nbtData;
		cachedRendering = null;
	}

	public @Nullable NbtCompound getNBTData() {
		return nbtData;
	}

	public void setName(@Nullable String name) {
		this.name = PlaceholderString.of(name);
		cachedRendering = null;
	}

	public @Nullable String getName() {
		if (name != null) {
			return name.getOriginalValue();
		} else {
			return null;
		}
	}

	public void setLore(@Nullable String... lore) {
		setLore(lore != null ? Arrays.asList(lore) : null);
	}

	public void setLore(@Nullable List<String> lore) {
		if (lore != null) {
			this.lore = new PlaceholderStringList(CollectionUtils.toArrayList(lore, element -> {
				return element != null ? element : "";
			}));
		} else {
			this.lore = null;
		}
		cachedRendering = null;
	}

	public @Nullable List<String> getLore() {
		if (lore != null) {
			return new ArrayList<>(lore.getOriginalValue());
		} else {
			return null;
		}
	}

	public void setEnchantments(@Nullable Map<Enchantment, Integer> enchantments) {
		this.enchantments = CollectionUtils.newHashMap(enchantments);
		cachedRendering = null;
	}

	public @Nullable Map<Enchantment, Integer> getEnchantments() {
		return CollectionUtils.newHashMap(enchantments);
	}

	public void addEnchantment(@NotNull Enchantment enchantment) {
		addEnchantment(enchantment, 1);
	}

	public void addEnchantment(@NotNull Enchantment enchantment, int level) {
		if (enchantments == null) {
			enchantments = new HashMap<>();
		}
		enchantments.put(enchantment, level);
		cachedRendering = null;
	}

	public void removeEnchantment(@NotNull Enchantment enchantment) {
		if (enchantments == null) {
			return;
		}
		enchantments.remove(enchantment);
		cachedRendering = null;
	}

	public @Nullable DyeColor getLeatherColor() {
		return leatherColor;
	}

	public void setLeatherColor(@Nullable DyeColor leatherColor) {
		this.leatherColor = leatherColor;
		cachedRendering = null;
	}

	public @Nullable String getSkullOwner() {
		if (skullOwner != null) {
			return skullOwner.getOriginalValue();
		} else {
			return null;
		}
	}

	public void setSkullOwner(@Nullable String skullOwner) {
		this.skullOwner = PlaceholderString.of(skullOwner);
		cachedRendering = null;
	}

	public @Nullable Map<BannerPattern, DyeColor> getBannerPatterns() {
		if (bannerPatterns==null) {
			return null;
		} else {
			return new HashMap<BannerPattern, DyeColor>(bannerPatterns);
		}
	}

	public void setBannerPatterns(@Nullable Map<BannerPattern, DyeColor> bannerPatterns) {
		if (bannerPatterns==null) {
			this.bannerPatterns = null;
		} else {
			this.bannerPatterns = new HashMap<BannerPattern, DyeColor>(bannerPatterns);
		}
		cachedRendering = null;
	}

	public boolean isPlaceholdersEnabled() {
		return placeholdersEnabled;
	}

	public void setPlaceholdersEnabled(boolean placeholdersEnabled) {
		this.placeholdersEnabled = placeholdersEnabled;
		cachedRendering = null;
	}

	public @Nullable Text renderName(ServerPlayerEntity viewer) {
		if (name == null) {
			return null;
		}
		if (!placeholdersEnabled) {
			return new LiteralText(name.getOriginalValue());
		}

		String name = this.name.getValue(viewer);

		if (name.isEmpty()) {
			// Add a color to display the name empty
			return new LiteralText("").setStyle(Style.EMPTY.withColor(Formatting.WHITE));
		} else {
			return new LiteralText(name);
		}
	}

	public @Nullable NbtList renderLore(ServerPlayerEntity viewer) {
		if (lore == null) {
			return null;
		}
		List<String> list;
		if (!placeholdersEnabled) {
			list = lore.getOriginalValue();
		} else {
			list = lore.getValue(viewer);
		}
		NbtList nbtList = new NbtList();
		nbtList.addAll(list.stream().map(s->new LiteralText(s)).map(t->NbtString.of(Text.Serializer.toJson(t))).collect(Collectors.toList()));
		return nbtList;
	}

	@Override
	public ItemStack render(@NotNull ServerPlayerEntity viewer) {
		if (shouldCacheRendering() && cachedRendering != null) {
			// Performance: return a cached item
			return cachedRendering;
		}

		ItemStack itemStack = new ItemStack(material, amount);
		itemStack.setDamage(durability);

		// First try to apply NBT data
		if (nbtData != null) {
			itemStack.setNbt(nbtData);
		}

		// Then apply data from config nodes, overwriting NBT data if there are conflicting values
		NbtCompound nbt = itemStack.getOrCreateNbt();
		
		itemStack.setCustomName(renderName(viewer));
		
		NbtList lore = renderLore(viewer);
		if (lore != null) {
			nbt.getCompound(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, lore);
		}
		
		Item item = itemStack.getItem();
		if (leatherColor != null && item instanceof DyeableItem) {
			DyeableItem.blendAndSetColor(itemStack, List.of(DyeItem.byColor(leatherColor)));
		}

		if (skullOwner != null && item instanceof SkullItem) {
			String skullOwner = this.skullOwner.getValue(viewer);
			nbt.putString(SkullItem.SKULL_OWNER_KEY, skullOwner);
		}

		if (item instanceof BannerItem) {
			NbtCompound bannerNbt = (NbtCompound) Objects.requireNonNullElse(nbt.get("BlockEntityTag"), nbt.put("BlockEntityTag", new NbtCompound()));
			if (bannerPatterns != null) {
				NbtList list = new NbtList();
				bannerPatterns.forEach((pattern, color)->{
					//TODO might need to do null check
					NbtCompound compound = new NbtCompound();
					compound.putInt("Color", color.getId());
					compound.putString("Pattern", pattern.getId());
					list.add(compound);
				});
				bannerNbt.put("Patterns", list);
			}

			// Hide all text details (damage, enchantments, potions, etc,)
			if (!nbt.contains("HideFlags", 99)||nbt.getInt("HideFlags")==0) {
				TooltipSection[] vals = TooltipSection.values();
				for (int i = 0; i < vals.length; i++) {
					itemStack.addHideFlag(vals[i]);
				}
			}
		}

		if (enchantments != null) {
			enchantments.forEach(itemStack::addEnchantment);
		}


		if (shouldCacheRendering()) {
			cachedRendering = itemStack;
		}

		return itemStack;
	}
}