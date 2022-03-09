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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.serialization.Serializer;
import megaminds.actioninventory.util.ValidationException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoadCommand {
	public static void register(LiteralArgumentBuilder<ServerCommandSource> root) {
		root.then(literal("load")
				.then(literal("opener")
						.then(argument("path", StringArgumentType.greedyString())
								.executes(LoadCommand::loadOpener)))
				.then(literal("inventory")
						.then(argument("path", StringArgumentType.greedyString())
								.executes(c->LoadCommand.loadInventory(c, false))
								.then(argument("override", BoolArgumentType.bool())
										.executes(c->LoadCommand.loadInventory(c, BoolArgumentType.getBool(c, "override")))))));
	}

	private static int loadOpener(CommandContext<ServerCommandSource> context) {
		var path = StringArgumentType.getString(context, "path");
		var p = Path.of(path);

		try (var br = Files.newBufferedReader(p)) {
			var opener = Serializer.openerFromJson(br);
			ActionInventoryMod.OPENER_LOADER.addOpener(opener);
			context.getSource().sendFeedback(new LiteralText("Loaded opener."), false);
			return 1;
		} catch (IOException e) {
			var msg = e.getMessage();
			context.getSource().sendError(new LiteralText("Unable to read file: "+p+". "+(msg!=null?msg:"")));
		} catch (ValidationException e) {
			var msg = e.getMessage();
			context.getSource().sendError(new LiteralText("Failed to create opener. "+(msg!=null?msg:"")));
		}
		return 0;
	}

	private static int loadInventory(CommandContext<ServerCommandSource> context, boolean override) {
		var path = StringArgumentType.getString(context, "path");
		var p = Path.of(path);

		try (var br = Files.newBufferedReader(p)) {
			var builder = Serializer.builderFromJson(br);
			if (override || !ActionInventoryMod.INVENTORY_LOADER.hasBuilder(builder.getName())) {
				ActionInventoryMod.INVENTORY_LOADER.addBuilder(builder);
				context.getSource().sendFeedback(new LiteralText("Loaded action inventory: "+builder.getName()), false);
				return 1;
			} else {
				context.getSource().sendError(new LiteralText("A loaded action inventory already has this name."));
			}
		} catch (IOException e) {
			var msg = e.getMessage();
			context.getSource().sendError(new LiteralText("Unable to read file: "+p+". "+(msg!=null&&!msg.equals(p.toString())?msg:"")));
		} catch (ValidationException e) {
			var msg = e.getMessage();
			context.getSource().sendError(new LiteralText("Failed to create action inventory. "+(msg!=null?msg:"")));
		}
		return 0;
	}
}