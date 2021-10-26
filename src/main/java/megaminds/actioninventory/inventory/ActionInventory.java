package megaminds.actioninventory.inventory;

import java.util.List;
import java.util.Set;

import megaminds.actioninventory.inventory.openers.Opener;
import megaminds.actioninventory.inventory.openers.Opener.ClickType;
import megaminds.actioninventory.inventory.openers.Opener.What;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ActionInventory implements Inventory {
	private int rows;
	private List<ActionItem> items;
	private List<Opener> openers;
	private Text displayName;
	private String name;
	private boolean disableCommand;
	private boolean disableAction;
	private boolean disableSign;

	public ActionItem getActionItem(int slot) {
		if (items==null) return ActionItem.EMPTY;
		for (ActionItem item : items) {
			if (item.getSlot() == slot) {
				return item;
			}
		}
		return ActionItem.EMPTY;
	}
	
	public boolean allowsCommand() {
		return !disableCommand;
	}

	public boolean allowsSign() {
		return !disableSign;
	}

	public boolean allowsAction() {
		return !disableAction;
	}

	public String getName() {
		return name;
	}
	
	public Text getDisplayName() {
		return displayName;
	}

	public boolean canOpen(ClickType click, What what, Object arg) {
		for (Opener o : openers) {
			if (o.canOpen(o, click, what)) {
				return true;
			}
		}
		return false;
	}

	public int getRows() {
		return rows;
	}

	@Override
	public void clear() {}

	@Override
	public int size() {
		return rows*9;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {}

	@Override
	public void markDirty() {}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public int getMaxCountPerStack() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void onOpen(PlayerEntity player) {
		if (player.world.isClient) return;
		ActionManager.onOpen((ServerPlayerEntity) player, this);
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (player.world.isClient) return;
		ActionManager.onClose((ServerPlayerEntity) player, this);
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public int count(Item item) {
		return 0;
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		return false;
	}
	
	@Override
	public String toString() {
		return "ActionInventory[rows="+rows+", name="+name+", displayName="+displayName+", items="+items+", openers="+openers+"]";
	}
}