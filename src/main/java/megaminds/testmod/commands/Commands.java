/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package megaminds.testmod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import megaminds.testmod.TestMod;
import megaminds.testmod.inventory.ActionManager;
import megaminds.testmod.inventory.openers.Opener.What;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class Commands {
	private static final DynamicCommandExceptionType CANT_OPEN_EXCEPTION = new DynamicCommandExceptionType(a->{
		return new LiteralText("Cannot Open Action Inventory: "+a.toString());
	});
	private static final SuggestionProvider<ServerCommandSource> SUGGESTER = (context, builder)->{
		return CommandSource.suggestMatching(ActionManager.getCommandInventoryNames(), builder);
	};
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean isDedicated) {
		dispatcher.register(literal(TestMod.MOD_ID).then(argument("name", string()).suggests(SUGGESTER).executes(Commands::execute)));
	}

	private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		String name = getString(context, "name");
		
		if (ActionManager.notify(ActionManager.getInventory(name), player, null, What.Command, null)) {
			return 1;
		} else {
			throw CANT_OPEN_EXCEPTION.create(name);
		}
	}   
}