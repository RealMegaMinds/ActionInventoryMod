package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.misc.Saver;
import net.minecraft.server.command.ServerCommandSource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Commands {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean isDedicated) {	//NOSONAR See net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
		LiteralArgumentBuilder<ServerCommandSource> root = literal(ActionInventoryMod.MOD_ID);
		//		root.redirect(null);	//redirect to help

		root.then(literal("reload").executes(Commands::reload));
		root.then(literal("save").executes(Commands::save));
		OpenCommand.register(root);

		dispatcher.register(root);
	}

	private static int save(CommandContext<ServerCommandSource> context) {
		Saver.saveAll();
		return 1;
	}
	private static int reload(CommandContext<ServerCommandSource> context) {
		ActionInventoryMod.reload(context.getSource().getServer());
		return 1;
	}
}