package megaminds.testmod.inventory.requirements;

import megaminds.testmod.inventory.storable.StoredRequirement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class XPRequirement implements Requirement {

	@Override
	public StoredRequirement getStoredRequirement() {
		return null;
	}

	@Override
	public boolean hasPaidFull(ServerPlayerEntity player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean acceptsPartialCost() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasFullCost(ServerPlayerEntity player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean takeCost(ServerPlayerEntity player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isViewRequirement() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Text getError() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void afterClick(ServerPlayerEntity player) {
		// TODO Auto-generated method stub
		
	}
}