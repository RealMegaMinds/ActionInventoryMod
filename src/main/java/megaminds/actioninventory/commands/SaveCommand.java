package megaminds.actioninventory.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import megaminds.actioninventory.misc.Saver;
import net.minecraft.server.command.ServerCommandSource;

public class SaveCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Saver.saveAll();
		return 1;
	}
}