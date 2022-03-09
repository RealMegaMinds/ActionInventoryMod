package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.loaders.ActionInventoryLoader;
import megaminds.actioninventory.serialization.Serializer;
import megaminds.actioninventory.util.ValidationException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoadCommand {
	public static void register(LiteralArgumentBuilder<ServerCommandSource> root) {
		root.then(literal("load")
				.then(argument("path", StringArgumentType.greedyString())
						.executes(LoadCommand::load)));
	}
	
	private static int remove(CommandContext<ServerCommandSource> context) {
		return 1;//TODO
	}
	
	private static int list(CommandContext<ServerCommandSource> context) {
		return 1;//TODO
	}

	private static int load(CommandContext<ServerCommandSource> context) {
		var path = StringArgumentType.getString(context, "path");
		var p = Path.of(path);
		
		try (var br = Files.newBufferedReader(p)) {
			var builder = Serializer.builderFromJson(br);
			if (!ActionInventoryMod.INVENTORY_LOADER.hasBuilder(builder.getName())) {
				ActionInventoryMod.INVENTORY_LOADER.addBuilder(builder);
				context.getSource().sendFeedback(new LiteralText("Loaded action inventory: "+builder.getName()), false);
				return 1;
			} else {
				context.getSource().sendError(new LiteralText("A loaded action inventory already has this name."));
			}
			return 1;
		} catch (IOException e) {
			var msg = e.getMessage();
			context.getSource().sendError(new LiteralText("Unable to read file: "+p+". "+(msg!=null?msg:"")));
			return 0;
		} catch (ValidationException e) {
			var msg = e.getMessage();
			context.getSource().sendError(new LiteralText("Failed to create action inventory. "+(msg!=null?msg:"")));
			return 0;
		}
	}
}