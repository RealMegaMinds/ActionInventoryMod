package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.ActionInventoryMod;
import net.minecraft.server.command.ServerCommandSource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Commands {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean isDedicated) {	//NOSONAR See net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
		LiteralArgumentBuilder<ServerCommandSource> root = literal(ActionInventoryMod.MOD_ID);
//		root.redirect(null);	//redirect to help
		
		root.then(literal("reload").executes(new ReloadCommand()));
		dispatcher.register(root);
	}
}