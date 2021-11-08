/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package megaminds.actioninventory.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.FileUtils;
import megaminds.actioninventory.inventory.helpers.ActionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text.Serializer;
import net.minecraft.util.Formatting;

public class Commands {
	private static final DynamicCommandExceptionType WRONG_NAME_EXCEPTION = new DynamicCommandExceptionType(a->{
		return new LiteralText("No Action Inventory With This Name: "+a.toString());
	});
	private static final SuggestionProvider<ServerCommandSource> COMMAND_SUGGESTER = (context, builder)->{
		return CommandSource.suggestMatching(ActionManager.getCommandInventoryNames(), builder);
	};
	private static final SuggestionProvider<ServerCommandSource> SIGN_SUGGESTER = (context, builder)->{
		return CommandSource.suggestMatching(ActionManager.getSignInventoryNames(), builder);
	};
	
	public static final String COMMAND_START = ActionInventoryMod.MOD_ID;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean isDedicated) {
		LiteralArgumentBuilder<ServerCommandSource> open = literal("open")
				.then(argument("name", string())
						.suggests(COMMAND_SUGGESTER)
						.executes(Commands::open));
		LiteralArgumentBuilder<ServerCommandSource> get = literal("get")
				.requires(source->source.hasPermissionLevel(4))
				.then(argument("name", string())
						.suggests(SIGN_SUGGESTER)
						.executes(Commands::get));
		LiteralArgumentBuilder<ServerCommandSource> start = literal(COMMAND_START)
				.then(open)
				.then(get);
		
		dispatcher.register(start);
	}

	private static int get(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		String name = FileUtils.stripExtension(getString(context, "name"));

		if (ActionManager.getInventory(name) != null) {
			ItemStack signStack = new ItemStack(Items.STICK);
			signStack.setCustomName(new LiteralText(name).setStyle(Style.EMPTY.withColor(Formatting.AQUA).withItalic(false)));
			NbtList lore = new NbtList();
			lore.add(NbtString.of(Serializer.toJson(new LiteralText("ActionInventory Maker").setStyle(Style.EMPTY.withItalic(false)))));
			signStack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, lore);
			player.getInventory().offerOrDrop(signStack);
			return 1;
		} else {
			throw WRONG_NAME_EXCEPTION.create(name);
		}
	}
	private static int open(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		String name = getString(context, "name");
		
		if (ActionManager.onCommand(player, name)) {
			return 1;
		} else {
			return 0;
		}
	}   
}