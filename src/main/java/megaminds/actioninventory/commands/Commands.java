package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.util.CommandPermissions;
import megaminds.actioninventory.util.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Commands {
	static final SuggestionProvider<ServerCommandSource> NAME_SUGGESTIONS = (c, b)->CommandSource.suggestIdentifiers(ActionInventoryMod.INVENTORY_LOADER.builderNames(), b);

	private Commands() {}
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) { //NOSONAR
		LiteralArgumentBuilder<ServerCommandSource> root = literal(ActionInventoryMod.MOD_ID);

		root.then(literal("list").requires(CommandPermissions.requires(root.getLiteral()+".list", 2)).executes(Commands::list));
		OpenCommand.register(root);
		LoadCommand.register(root);
		RemoveCommand.register(root);

		dispatcher.register(root);
	}

	private static int list(CommandContext<ServerCommandSource> context) {
		var names = ActionInventoryMod.INVENTORY_LOADER.builderNames();
		var size = names.size();
		var combined = new StringBuilder(size*10);
		names.forEach(i->combined.append("\n"+i.toString()));
		
		//TODO Figure out if there is a reason for below
		var message = Text.empty().append(MessageHelper.toSuccess(size+" Action Inventories are loaded."));
		//instead of
		//var message = MessageHelper.toSuccess(size+" Action Inventories are loaded.");
		
		if (size>0) message.append(combined.toString());
		context.getSource().sendFeedback(message, false);
		return size;
	}
}