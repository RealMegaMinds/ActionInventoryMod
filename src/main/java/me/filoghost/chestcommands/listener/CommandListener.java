/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.listener;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import me.filoghost.chestcommands.menu.InternalMenu;
import me.filoghost.chestcommands.menu.MenuManager;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class CommandListener {
	private static final DynamicCommandExceptionType WRONG_NAME_EXCEPTION = new DynamicCommandExceptionType(a->{
		return new LiteralText("No menu with this name: "+a.toString());
	});
	private static final SuggestionProvider<ServerCommandSource> SUGGESTER = (context, builder)->{
		return CommandSource.suggestMatching(MenuManager.getOpenCommands().stream().map(s->s.getOriginalString()), builder);
	};
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean isDedicated) {
		dispatcher.register(literal("chestcommands").then(argument("menuName", greedyString()).suggests(SUGGESTER).executes(CommandListener::execute)));
	}

	private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		String name = getString(context, "menuName");
		InternalMenu menu = MenuManager.getMenuByOpenCommand(name);

		if (menu == null) {
			throw WRONG_NAME_EXCEPTION.create(name);
		} else if (menu.openCheckingPermission(player)) {
			return 1;
		}
		return -1;
	}   
}