package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.util.CommandPermissions;
import megaminds.actioninventory.util.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Commands {
	private Commands() {}
	
	@SuppressWarnings("unused")	//Used as CommandRegistrationCallback
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
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
		
		//Append to empty Text so success formatting doesn't carry to other appended texts
		var message = Text.empty().append(MessageHelper.toSuccess(size+" Action Inventories are loaded."));
		
		if (size>0) message.append(combined.toString());
		context.getSource().sendFeedback(() -> message, false);
		return size;
	}
}