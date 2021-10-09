/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import me.filoghost.chestcommands.parsing.ItemStackParser;
import me.filoghost.chestcommands.parsing.ParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class GiveItemAction implements Action {

	private final ItemStack itemToGive;

	public GiveItemAction(String serializedAction) throws ParseException {
		ItemStackParser reader = new ItemStackParser(serializedAction, true);
		reader.checkNotAir();
		itemToGive = reader.createStack();
	}

	@Override
	public void execute(ServerPlayerEntity player) {
		player.getInventory().offerOrDrop(itemToGive.copy());
	}
}