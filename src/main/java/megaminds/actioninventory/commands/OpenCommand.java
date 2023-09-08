package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Collection;
import java.util.List;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.util.CommandPermissions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OpenCommand {
	private static final SuggestionProvider<ServerCommandSource> NAME_SUGGESTIONS = (c, b) -> CommandSource.suggestIdentifiers(ActionInventoryMod.INVENTORY_LOADER.builderNames(builder -> builder.canOpen(c.getSource().getPlayer())), b);;

	private OpenCommand() {}

	private static final String SILENT_ARG = "silent";

	public static void register(LiteralArgumentBuilder<ServerCommandSource> root) {
		root.then(literal("open")
				.then(argument("guiName", IdentifierArgumentType.identifier())
						.suggests(NAME_SUGGESTIONS)
						.executes(OpenCommand::open)
						.then(argument(SILENT_ARG, BoolArgumentType.bool())
								.executes(OpenCommand::open))
						.then(argument("targets", EntityArgumentType.players())
								.requires(CommandPermissions.requires(root.getLiteral()+".open", 2))
								.executes(OpenCommand::open)
								.then(argument(SILENT_ARG, BoolArgumentType.bool())
										.executes(OpenCommand::open)))));
	}

	private static int open(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> targets;
		try {
			targets = EntityArgumentType.getOptionalPlayers(context, "targets");
		} catch (IllegalArgumentException e) {
			targets = List.of(context.getSource().getPlayerOrThrow());
		}

		boolean silent = false;
		try {
			silent = BoolArgumentType.getBool(context, SILENT_ARG);
		} catch (IllegalArgumentException e) {
			//default
		}

		var name = IdentifierArgumentType.getIdentifier(context, "guiName");
		var builder = ActionInventoryMod.INVENTORY_LOADER.getBuilder(name);

		if (builder==null) {
			context.getSource().sendError(Text.of("No Action Inventory with name: "+name));
			return 0;
		}

		var success = 0;
		for (var target : targets) {
			if (builder.buildAndOpen(target)) {
				success++;
				if (!silent) context.getSource().sendFeedback(() -> Text.literal("Opened "+name+" for ").append(target.getName()), false);
			} else if (!silent) {
				context.getSource().sendError(Text.literal("Failed to open "+name+" for ").append(target.getName()));
			}
		}
		return success;
	}
}
