package megaminds.actioninventory.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import megaminds.actioninventory.ActionInventoryMod;
import net.minecraft.server.command.ServerCommandSource;

public class ReloadCommand implements Command<ServerCommandSource>{
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ActionInventoryMod.reload(context.getSource().getServer());
		return 0;
	}
}