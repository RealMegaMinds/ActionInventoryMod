package megaminds.testmod.permissions;

import net.minecraft.server.network.ServerPlayerEntity;

public class VanillaPermissions implements Permissions {

	@Override
	public boolean checkPermission(ServerPlayerEntity player, Perm p) {
		return true;
	}
}