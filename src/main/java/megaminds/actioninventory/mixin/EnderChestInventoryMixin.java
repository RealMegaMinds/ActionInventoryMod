package megaminds.actioninventory.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(EnderChestInventory.class)
public class EnderChestInventoryMixin {
	private ServerPlayerEntity owner;
	
	public ServerPlayerEntity getOwner() {
		return owner;
	}
	
	public void setOwner(ServerPlayerEntity owner) {
		if (this.owner==null) this.owner = owner;
	}
}