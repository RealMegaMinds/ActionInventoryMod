package megaminds.actioninventory.util;

import java.util.function.Predicate;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;

public interface CommandPermissions {
	CommandPermissions INSTANCE = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0") ? new FabricPermissions() : new VanillaPermissions();

	static Predicate<ServerCommandSource> requires(String command, int def) {
		return s->INSTANCE.hasPermission(s, command, def);
	}

	boolean hasPermission(ServerCommandSource source, String command, int def);

	final class VanillaPermissions implements CommandPermissions {
		@Override
		public boolean hasPermission(ServerCommandSource source, String command, int def) {
			return source.hasPermissionLevel(def);
		}
	}

	final class FabricPermissions implements CommandPermissions {
		@Override
		public boolean hasPermission(ServerCommandSource source, String command, int def) {
			return Permissions.check(source, command, def);
		}
	}
}