package megaminds.actioninventory;

import net.minecraft.server.network.ServerPlayerEntity;

public interface OwnerHolder {
	ServerPlayerEntity getOwner();
	void setOwner(ServerPlayerEntity owner);
}