package megaminds.testmod.inventory.requirements;

import net.minecraft.server.network.ServerPlayerEntity;

public class XPRequirement extends Requirement {
	private int requiredCost;
	private boolean consumes;
	private boolean allowPartial;
	private boolean resets;

	@Override
	public boolean pay(ServerPlayerEntity player) {
		int paid = (int) RequirementStorageManager.getPayment(this, player, 0);
		if (paid >= requiredCost) return true;

		int level = player.experienceLevel;
		if (paid+level >= requiredCost) {
			if (consumes) {
				player.addExperience(-(requiredCost-paid));
			}
			return true;
		} else if (allowPartial && consumes) {
			player.addExperience(-level);
			RequirementStorageManager.setPayment(this, player, paid+level);
		}
		return false;
	}

	@Override
	protected Type getTypeInternal() {
		return Type.XP;
	}

	@Override
	public void afterSuccess(ServerPlayerEntity player) {
		if (resets) {
			RequirementStorageManager.removePayment(this, player);
		}
	}
}