package megaminds.testmod.inventory;

import java.util.List;

import com.google.common.collect.ImmutableList;

import megaminds.testmod.MessageHelper;
import megaminds.testmod.actions.Action;
import megaminds.testmod.inventory.requirements.Requirement;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryItem {
	private final ImmutableList<Requirement> requirements;
	private final ImmutableList<Action>	actions;
	private final ItemStack representation;
	private final boolean closeAfterClick;
	
	public InventoryItem(ItemStack representation, List<Action> actions, List<Requirement> requirements, boolean closeAfterClick) {
		this.requirements = ImmutableList.copyOf(requirements);
		this.actions = ImmutableList.copyOf(actions);
		this.representation = representation.copy();
		this.closeAfterClick = closeAfterClick;
	}

	public ItemStack asItemStack(ServerPlayerEntity player) {
		for (Requirement r : requirements) {
			if (r.isViewRequirement() && !r.tryPay(player)) {
				MessageHelper.toPlayerMessage(player, r.getError(), true);
				return ItemStack.EMPTY;
			}
		}
		return representation;
	}
	
	public void onClick(ServerPlayerEntity player) {
		requirements.forEach(r -> {
			if (!r.isViewRequirement() && !r.tryPay(player)) {
				MessageHelper.toPlayerMessage(player, r.getError(), true);
				return;
			}
		});
		for (Action action : actions) {
			action.execute(player);
		}
		if (closeAfterClick) {
			player.closeHandledScreen();
		}
		requirements.forEach(r -> {
			r.afterClick(player);
		});
	}
}