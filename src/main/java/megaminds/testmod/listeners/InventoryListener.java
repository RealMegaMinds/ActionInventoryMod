/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package megaminds.testmod.listeners;

import megaminds.testmod.EditableClickSlotC2SPacket;
import megaminds.testmod.inventory.ActionInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class InventoryListener {
	public static TypedActionResult<ClickSlotC2SPacket> onInventoryClick(ClickSlotC2SPacket packet, ServerPlayerEntity player) {
		Inventory inventory = player.currentScreenHandler.getSlot(packet.getSlot()).inventory;
		if (packet.getSlot()==ScreenHandler.EMPTY_SPACE_SLOT_INDEX || !(inventory instanceof ActionInventory)) {
			return TypedActionResult.pass(packet);
		}

		packet = new EditableClickSlotC2SPacket(packet).modifiedStacks(new Int2ObjectOpenHashMap<>()).asPacket();
		player.currentScreenHandler.nextRevision();

		ActionInventory actionInv = (ActionInventory) inventory;
		boolean success = actionInv.onClicked(player, packet.getSlot());
		if (success) {
			return TypedActionResult.success(packet);
		} else {
			return TypedActionResult.fail(packet);
		}
	}
}