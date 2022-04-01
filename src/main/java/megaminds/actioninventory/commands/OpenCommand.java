package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.util.CommandPermissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenCommand {
	public static void register(LiteralArgumentBuilder<ServerCommandSource> root) {
		root.then(literal("open")
				.requires(CommandPermissions.requires(root.getLiteral()+".open", 3))
				.then(argument("targets", EntityArgumentType.players())
						.then(argument("guiName", IdentifierArgumentType.identifier())
								.suggests(Commands.NAME_SUGGESTIONS)
								.executes(c->open(c, false))
								.then(argument("silent", BoolArgumentType.bool())
										.executes(c->open(c, BoolArgumentType.getBool(c, "silent")))))));
	}

	private static int open(CommandContext<ServerCommandSource> context, boolean silent) throws CommandSyntaxException {
		var targets = EntityArgumentType.getOptionalPlayers(context, "targets");
		var name = IdentifierArgumentType.getIdentifier(context, "guiName");
		var builder = ActionInventoryMod.INVENTORY_LOADER.getBuilder(name);

		if (builder==null) {
			context.getSource().sendError(new LiteralText("No Action Inventory with name: "+name));
			return 0;
		}

		var success = 0;
		for (var target : targets) {
			if (builder.build(target).open()) {
				success++;
				if (!silent) context.getSource().sendFeedback(new LiteralText("Opened "+name+" for ").append(target.getName()), false);
			} else if (!silent) {
				context.getSource().sendError(new LiteralText("Failed to open "+name+" for ").append(target.getName()));
			}
		}
		return success;
	}
}