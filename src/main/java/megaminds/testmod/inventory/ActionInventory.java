package megaminds.testmod.inventory;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class ActionInventory implements Inventory, NamedScreenHandlerFactory {
	private static final Map<String, ActionInventory> ALL_INVENTORIES = new HashMap<>();	//TODO probably should initialize this from files
	
	public static final ActionInventory getInventory(String name) {
		return ALL_INVENTORIES.get(name);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemStack getStack(int slot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack removeStack(int slot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void markDirty() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Text getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

}
