package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.ActionInventoryMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Texts;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Commands {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean isDedicated) {	//NOSONAR See net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
		LiteralArgumentBuilder<ServerCommandSource> root = literal(ActionInventoryMod.MOD_ID);
		//		root.redirect(null);	//redirect to help

		root.then(literal("reload").executes(Commands::reload));
		root.then(literal("list").executes(Commands::list));
		OpenCommand.register(root);
		LoadCommand.register(root);

		dispatcher.register(root);
	}

	//	private static int remove(CommandContext<ServerCommandSource> context) {
	//		return 1;//TODO
	//	}
	
	private static int reload(CommandContext<ServerCommandSource> context) {
		var manager = context.getSource().getServer().getResourceManager();
		ActionInventoryMod.INVENTORY_LOADER.reload(manager);
		ActionInventoryMod.OPENER_LOADER.reload(manager);
		return 1;
	}
	
	private static int list(CommandContext<ServerCommandSource> context) {
		var names = ActionInventoryMod.INVENTORY_LOADER.builderNames();
		var size = names.size();
		var text = Texts.join(names, new LiteralText("\n"), i->new LiteralText(i.toString()));
		context.getSource().sendFeedback(new LiteralText(size+" Action Inventories are loaded.\n").append(text), false);
		return size;
	}
}




















