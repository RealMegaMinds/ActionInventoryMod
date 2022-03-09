package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.ActionInventoryMod;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoveCommand {
	public static void register(LiteralArgumentBuilder<ServerCommandSource> root) {
		root.then(literal("remove")
				.then(argument("guiName", IdentifierArgumentType.identifier())
						.suggests(Commands.NAME_SUGGESTIONS)
						.executes(RemoveCommand::remove)));
	}

	private static int remove(CommandContext<ServerCommandSource> context) {
		var name = IdentifierArgumentType.getIdentifier(context, "guiName");

		if (!ActionInventoryMod.INVENTORY_LOADER.hasBuilder(name)) {
			context.getSource().sendError(new LiteralText("No Action Inventory with name: "+name));
			return 0;
		}

		ActionInventoryMod.INVENTORY_LOADER.removeBuilder(name);
		context.getSource().sendFeedback(new LiteralText("Removed Action Inventory with name: "+name), false);
		return 1;
	}
}