package megaminds.testmod.inventory;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ActionInventory implements Inventory, NamedScreenHandlerFactory {
	private final String name;
	private final int rows;
	private final OpenChecker openChecker;
	private final ImmutableList<InventoryItem> items;

	private ActionInventory() {
		this.name = null;
		this.rows = -1;
		this.openChecker = null;
		this.items = null;
	}

	private ActionInventory(String name, int rows, List<InventoryItem> items, OpenChecker openChecker) {
		this.name = name;
		this.rows = rows;
		this.openChecker = openChecker;
		this.items = ImmutableList.copyOf(items);
	}

	public static ActionInventory getNewInventory(String name) {
		if (ActionInventoryManager.getInventory(name)!=null) {
			return null;//new ActionInventory(name);
		}
		return null;
	}

	public OpenChecker getOpenChecker() {
		return openChecker;
	}

	public String getName() {
		return name;
	}

	public int getRows() {
		return rows;
	}

	public ImmutableList<InventoryItem> getItems() {
		return items;
	}

	@Override
	public void onOpen(PlayerEntity player) {
		if (player.world.isClient) return;
		ActionInventoryManager.onOpen((ServerPlayerEntity) player, this);
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (player.world.isClient) return;
		ActionInventoryManager.onClose((ServerPlayerEntity) player, this);
	}

	//many things can be defaulted
}