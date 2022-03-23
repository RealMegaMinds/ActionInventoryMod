package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.ActionInventoryMod;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Texts;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Commands {
	static final SuggestionProvider<ServerCommandSource> NAME_SUGGESTIONS = (c, b)->CommandSource.suggestIdentifiers(ActionInventoryMod.INVENTORY_LOADER.builderNames(), b);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean isDedicated) {	//NOSONAR See net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
		LiteralArgumentBuilder<ServerCommandSource> root = literal(ActionInventoryMod.MOD_ID);

		root.then(literal("list").executes(Commands::list));
		OpenCommand.register(root);
		LoadCommand.register(root);
		RemoveCommand.register(root);

		dispatcher.register(root);
	}

	private static int list(CommandContext<ServerCommandSource> context) {
		var names = ActionInventoryMod.INVENTORY_LOADER.builderNames();
		var size = names.size();
		var text = Texts.join(names, new LiteralText("\n"), i->new LiteralText(i.toString()));
		context.getSource().sendFeedback(new LiteralText(size+" Action Inventories are loaded.\n").append(text), false);
		return size;
	}
}