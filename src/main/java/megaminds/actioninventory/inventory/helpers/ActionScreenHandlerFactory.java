package megaminds.actioninventory.inventory.helpers;

import megaminds.actioninventory.inventory.ActionInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ActionScreenHandlerFactory implements NamedScreenHandlerFactory {
	private ActionInventory inventory;
	
	public ActionScreenHandlerFactory(ActionInventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new ActionScreenHandler(inventory, (ServerPlayerEntity)playerEntity, i);
	}

	@Override
	public Text getDisplayName() {
		return inventory.getDisplayName();
	}
}