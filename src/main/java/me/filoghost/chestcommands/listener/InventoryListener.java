/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.listener;

import me.filoghost.chestcommands.api.Icon;
import me.filoghost.chestcommands.api.Menu;
import me.filoghost.chestcommands.config.Settings;
import me.filoghost.chestcommands.inventory.DefaultMenuView;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.menu.InternalMenu;
import me.filoghost.chestcommands.menu.MenuManager;
import me.filoghost.fcommons.logging.Log;
import megaminds.testmod.EditableClickSlotC2SPacket;
import megaminds.testmod.Helper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import org.bukkit.event.block.Action;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Map;
import java.util.WeakHashMap;

public class InventoryListener {
	private final static Map<ServerPlayerEntity, Long> antiClickSpam = new WeakHashMap<>();
	
	public static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult){
		return onInteract(player, world, hand, Action.RIGHT_CLICK_BLOCK);
	}
	
	public static TypedActionResult<ItemStack> onItemUse(PlayerEntity player, World world, Hand hand) {
		return new TypedActionResult<ItemStack>(onInteract(player, world, hand, Action.RIGHT_CLICK_AIR), ItemStack.EMPTY);
	}

	public static ActionResult onBlockAttack(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		return onInteract(player, world, hand, Action.LEFT_CLICK_BLOCK);
	}

	private static ActionResult onInteract(PlayerEntity player, World world, Hand hand, Action action) {
		if (!world.isClient) {
			ItemStack stack = player.getStackInHand(hand);
			if (stack.getItem()!=Items.AIR) {
				MenuManager.openMenuByItem((ServerPlayerEntity)player, stack, action);
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}
	
	public static TypedActionResult<ClickSlotC2SPacket> onInventoryClick(ClickSlotC2SPacket packet, ServerPlayerEntity player) {
		DefaultMenuView menuView;
		if (packet.getSlot()==ScreenHandler.EMPTY_SPACE_SLOT_INDEX || (menuView = MenuManager.getOpenMenuView(player.currentScreenHandler.getSlot(packet.getSlot()).inventory))==null) {
			return TypedActionResult.pass(packet);
		}
		
		player.currentScreenHandler.nextRevision();
		packet = new EditableClickSlotC2SPacket(packet).modifiedStacks(new Int2ObjectOpenHashMap<>()).asPacket();


		Icon icon = menuView.getIcon(packet.getSlot());
		if (icon == null) {
			return TypedActionResult.fail(packet);
		}

		Long cooldownUntil = antiClickSpam.get(player);
		long now = System.currentTimeMillis();
		int minDelay = Settings.get().anti_click_spam_delay;

		if (minDelay > 0) {
			if (cooldownUntil != null && cooldownUntil > now) {
				return TypedActionResult.fail(packet);
			} else {
				antiClickSpam.put(player, now + minDelay);
			}
		}
		
		try {
			icon.onClick(menuView, player);
		} catch (Throwable t) {
			handleIconClickException(player, menuView.getMenu(), t);
			menuView.close();
		}
		
		player.currentScreenHandler.nextRevision();
		return TypedActionResult.success(packet);
	}

	private static void handleIconClickException(ServerPlayerEntity clicker, Menu menu, Throwable throwable) {
		Log.severe("Encountered an exception while handling a click inside the menu \"" + Errors.formatPath(((InternalMenu) menu).getSourceFile()) + "\"", throwable);
		Helper.serverToPlayerMessage(new LiteralText("An internal error occurred when you clicked on the item.").setStyle(Style.EMPTY.withColor(Formatting.RED)), clicker);
	}
}