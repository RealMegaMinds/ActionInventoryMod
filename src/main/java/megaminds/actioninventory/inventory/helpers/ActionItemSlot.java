package megaminds.actioninventory.inventory.helpers;

import java.util.Optional;

import megaminds.actioninventory.inventory.ActionInventory;
import megaminds.actioninventory.inventory.ActionItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class ActionItemSlot extends Slot {
	private ActionItem item;
	private ItemStack stack;
	private ServerPlayerEntity player;

	public ActionItemSlot(ActionInventory inventory, ServerPlayerEntity player, int index, int x, int y) {
		super(inventory, index, x, y);
		this.item = inventory.getActionItem(index);
		this.stack = item.getRepresentation(player);
		this.player = player;
	}
	
	public void onClick() {
		item.onClick(player);
	}
	
	@Override
	public void onQuickTransfer(ItemStack newItem, ItemStack original) {}

	@Override
	public void onTakeItem(PlayerEntity player, ItemStack stack) {}

	@Override
	public boolean canInsert(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack getStack() {
		return stack.copy();
	}

	@Override
	public boolean hasStack() {
		return !stack.isEmpty();
	}

	@Override
	public void setStack(ItemStack stack) {}

	@Override
	public void markDirty() {}

	@Override
	public ItemStack takeStack(int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canTakeItems(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	public Optional<ItemStack> tryTakeStackRange(int min, int max, PlayerEntity player) {
		return Optional.of(ItemStack.EMPTY);
	}

	@Override
	public ItemStack takeStackRange(int min, int max, PlayerEntity player) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertStack(ItemStack stack) {
		return stack;
	}

	@Override
	public ItemStack insertStack(ItemStack stack, int count) {
		return stack;
	}

	@Override
	public boolean canTakePartial(PlayerEntity player) {
		return false;
	}
}