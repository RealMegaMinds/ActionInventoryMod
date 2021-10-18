package megaminds.testmod.inventory.viewable;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import megaminds.testmod.inventory.storable.StoredActionInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ViewableActionInventory implements Inventory, NamedScreenHandlerFactory {
	private final StoredActionInventory internalInventory;
	private final int size;
	private final ImmutableList<ItemStack> stacks;

	public ViewableActionInventory(StoredActionInventory inv) {
		this.internalInventory = inv;
		this.size = inv.rows * 9;
		this.stacks = ImmutableList.copyOf(inv.items.stream().map(item->ItemStack.fromNbt(item.itemStack)).toArray(ItemStack[]::new));
		ViewableManager.onCreate(inv.name, this);
	}

	@Override
	public void clear() {}

	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		ScreenHandlerType<GenericContainerScreenHandler> type;
		switch (internalInventory.rows) {
		case 1:
			type = ScreenHandlerType.GENERIC_9X1;
		case 2:
			type = ScreenHandlerType.GENERIC_9X2;
		case 3:
			type = ScreenHandlerType.GENERIC_9X3;
		case 4:
			type = ScreenHandlerType.GENERIC_9X4;
		case 5:
			type = ScreenHandlerType.GENERIC_9X5;
		case 6:
			type = ScreenHandlerType.GENERIC_9X6;
		default:
			type = null;
		}
		return new GenericContainerScreenHandler(type, i, playerInventory, this, internalInventory.rows);
	}

	@Override
	public Text getDisplayName() {
		return internalInventory.name;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return stacks.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return Objects.requireNonNullElse(stacks.get(slot), ItemStack.EMPTY);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {return ItemStack.EMPTY;}

	@Override
	public ItemStack removeStack(int slot) {return ItemStack.EMPTY;}

	@Override
	public void setStack(int slot, ItemStack stack) {}

	@Override
	public void markDirty() {}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void onOpen(PlayerEntity player) {
		if (!player.world.isClient) {
			ViewableManager.onOpen((ServerPlayerEntity) player, this);
		}
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (!player.world.isClient) {
			ViewableManager.onClose((ServerPlayerEntity) player, this);
		}
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
}