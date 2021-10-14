package megaminds.testmod.permissions;

import net.minecraft.server.network.ServerPlayerEntity;

public interface Permissions {
	public static final Permissions INSTANCE = getInstance();
	
	static Permissions getInstance() {
		if (INSTANCE != null) {
			return INSTANCE;
		}
		//could check if another mod adds permission systems here (such as luckperms)
		return new VanillaPermissions();
	}
	
	enum Perm {
		CREATE_INV
	}
	
	public static boolean hasPermission(ServerPlayerEntity player, Perm p) {
		return INSTANCE.checkPermission(player, p);
	}
	
	boolean checkPermission(ServerPlayerEntity player, Perm p);
	//need to add methods for each permission check I think
}