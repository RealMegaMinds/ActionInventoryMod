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

import megaminds.testmod.MessageHelper;
import megaminds.testmod.TestMod;
import megaminds.testmod.inventory.ActionInventory;
import megaminds.testmod.inventory.ActionInventoryManager;
import megaminds.testmod.inventory.OpenChecker;
import megaminds.testmod.inventory.OpenChecker.OpenType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class Commands {
	private static final DynamicCommandExceptionType WRONG_NAME_EXCEPTION = new DynamicCommandExceptionType(a->{
		return new LiteralText("No Action Inventory Called: "+a.toString());
	});
	private static final DynamicCommandExceptionType CANT_OPEN_EXCEPTION = new DynamicCommandExceptionType(a->{
		return new LiteralText("Cannot Open Action Inventory Called: "+a.toString());
	});
	private static final SuggestionProvider<ServerCommandSource> SUGGESTER = (context, builder)->{
		return CommandSource.suggestMatching(ActionInventoryManager.getOpenInventoryNames(), builder);
	};
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean isDedicated) {
		dispatcher.register(literal(TestMod.MOD_ID).then(argument("name", string()).suggests(SUGGESTER).executes(Commands::execute)));
	}

	private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		String name = getString(context, "name");
		
		ActionInventory inv = ActionInventoryManager.getInventory(name);
		if (inv==null) {
			throw WRONG_NAME_EXCEPTION.create(name);
		} else if (OpenChecker.check(inv.getOpenChecker(), OpenType.COMMAND, null, null)) {
			ActionInventoryManager.open(inv, player);
			return 1;
		} else {
			throw CANT_OPEN_EXCEPTION.create(name);
		}
	}   
}