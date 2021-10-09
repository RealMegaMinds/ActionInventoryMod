/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.jetbrains.annotations.Nullable;

public final class MaterialsHelper {
	public static @Nullable Item matchMaterial(String materialName) {
		return Registry.ITEM.get(new Identifier(materialName));
	}

	public static boolean isAir(Item material) {
		return material==Items.AIR;
	}
}