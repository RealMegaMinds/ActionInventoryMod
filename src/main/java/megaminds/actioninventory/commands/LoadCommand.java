package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.util.CommandPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class LoadCommand {
	private LoadCommand() {}
	
	public static void register(LiteralArgumentBuilder<ServerCommandSource> root) {
		root.then(literal("load")
				.requires(CommandPermissions.requires(root.getLiteral()+".load", 4))
				.then(literal("opener")
						.then(argument("path", StringArgumentType.string())
								.executes(LoadCommand::loadOpener)))
				.then(literal("inventory")
						.then(argument("path", StringArgumentType.string())
								.executes(c->LoadCommand.loadInventory(c, false))
								.then(argument("override", BoolArgumentType.bool())
										.executes(c->LoadCommand.loadInventory(c, BoolArgumentType.getBool(c, "override")))))));
	}

	private static int loadOpener(CommandContext<ServerCommandSource> context) {
		var path = StringArgumentType.getString(context, "path");
		var p = Path.of(path);

		try (var br = Files.newBufferedReader(p)) {
			if (ActionInventoryMod.OPENER_LOADER.loadOpener(p.toString(), JsonHelper.deserialize(br))) {
				context.getSource().sendFeedback(() -> Text.of("Loaded opener."), false);
				return 1;
			}
		} catch (IOException e) {
			var msg = e.getMessage();
			context.getSource().sendError(Text.of("Unable to read file: "+p+". "+(msg!=null?msg:"")));
		}
		return 0;
	}

	private static int loadInventory(CommandContext<ServerCommandSource> context, boolean override) {
		var path = StringArgumentType.getString(context, "path");
		var p = Path.of(path);

		try (var br = Files.newBufferedReader(p)) {
			var builder = ActionInventoryMod.INVENTORY_LOADER.loadBuilder("path", br);
			if (builder.isPresent()) {
				//TODO: Should force builder and path name to be the same. Would then be able to check if there's already a builder without having to load it.
				if (override || !ActionInventoryMod.INVENTORY_LOADER.hasBuilder(builder.orElseThrow().getName())) {
					ActionInventoryMod.INVENTORY_LOADER.addBuilder(builder.orElseThrow());
					context.getSource().sendFeedback(() -> Text.of("Loaded action inventory: "+builder.orElseThrow().getName()), false);
					return 1;
				}

				context.getSource().sendError(Text.of("A loaded action inventory already has this name."));
			} else {
				context.getSource().sendError(Text.of("Failed to create action inventory."));
			}
		} catch (IOException e) {
			context.getSource().sendError(Text.of("Unable to read file: "+p+". "+e.toString()));
		}
		return 0;
	}
}