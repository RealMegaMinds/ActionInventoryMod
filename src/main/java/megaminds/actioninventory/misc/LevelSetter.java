package megaminds.actioninventory.misc;

import net.minecraft.server.command.ServerCommandSource;

public interface LevelSetter {
	ServerCommandSource withHigherLevel(int level);
}