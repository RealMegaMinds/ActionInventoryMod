package megaminds.actioninventory.inventory;

import java.util.List;

import megaminds.actioninventory.inventory.actions.Action;
import megaminds.actioninventory.inventory.requirements.Requirement;
import megaminds.actioninventory.inventory.requirements.Requirement.When;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class ActionItem {
	public static final ActionItem EMPTY = new ActionItem();
	
	private int slot;
	private List<Action> actions;
	private List<Requirement> requirements;
	private ItemStack representation;
	
	public int getSlot() {
		return slot;
	}
	
	public ItemStack getRepresentation(ServerPlayerEntity player) {
		if (representation==null || requirements==null) return ItemStack.EMPTY;
		
		if (!canView(player)) {
			return ItemStack.EMPTY;
		}
		
		for (Requirement r : requirements) {
			if (r.getWhen()==When.VIEW) {
				r.afterSuccess(player);
			}
		}
		return representation;
	}
	
	private boolean canView(ServerPlayerEntity player) {
		for (Requirement r : requirements) {
			if (r.getWhen()==When.VIEW && !r.pay(player)) {
				return false;
			}
		}
		return true;
	}
	
	public void onClick(ServerPlayerEntity player) {
		if (!canView(player) || requirements==null || actions==null) return;
		
		boolean canUse = true;
		for (Requirement r : requirements) {
			if (r.getWhen()==When.CLICK && !r.pay(player)) {
				canUse = false;
			}
		}
		
		if (!canUse) {
			return;
		}
		
		for (Action a : actions) {
			a.execute(player);
		}
		
		for (Requirement r : requirements) {
			if (r.getWhen()==When.CLICK) {
				r.afterSuccess(player);
			}
		}
	}
}