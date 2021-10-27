package megaminds.actioninventory.inventory.helpers;

import megaminds.actioninventory.inventory.ActionInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

public class ActionScreenHandler extends ScreenHandler {
	private ActionInventory inventory;
	private ServerPlayerEntity player;

	protected ActionScreenHandler(ActionInventory inventory, ServerPlayerEntity player, int syncId) {
		super(getType(inventory.getRows()), syncId);
		int rows = inventory.getRows();
		checkSize(inventory, rows * 9);
		this.inventory = inventory;
		this.player = player;

		inventory.onOpen(this.player);
		int k = (rows - 4) * 18;

		int p;
		int o;
		for(p = 0; p < rows; ++p) {
			for(o = 0; o < 9; ++o) {
				this.addSlot(new ActionItemSlot(inventory, player, o + p * 9, 8 + o * 18, 18 + p * 18));
			}
		}

		for(p = 0; p < 3; ++p) {
			for(o = 0; o < 9; ++o) {
				this.addSlot(new Slot(player.getInventory(), o + p * 9 + 9, 8 + o * 18, 103 + p * 18 + k));
			}
		}

		for(p = 0; p < 9; ++p) {
			this.addSlot(new Slot(player.getInventory(), p, 8 + p * 18, 161 + k));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return inventory.canPlayerUse(player);
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.inventory.onClose(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		Slot s = slotIndex==EMPTY_SPACE_SLOT_INDEX ? null : getSlot(slotIndex);
		if (s instanceof ActionItemSlot) {
			((ActionItemSlot)s).onClick();
		}
		super.onSlotClick(slotIndex, button, actionType, player);
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.canInsert(stack);
	}

	@Override
	protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
		boolean bl = false;
		int i = startIndex;
		if (fromLast) {
			i = endIndex - 1;
		}

		Slot slot2;
		ItemStack itemStack;
		if (stack.isStackable()) {
			while(!stack.isEmpty()) {
				Before:
				{
					if (fromLast) {
						if (i < startIndex) {
							break;
						}
					} else if (i >= endIndex) {
						break;
					}

					slot2 = (Slot)this.slots.get(i);

					if (slot2 instanceof ActionItemSlot) {
						break Before;
					}

					itemStack = slot2.getStack();
					if (!itemStack.isEmpty() && ItemStack.canCombine(stack, itemStack)) {
						int j = itemStack.getCount() + stack.getCount();
						if (j <= stack.getMaxCount()) {
							stack.setCount(0);
							itemStack.setCount(j);
							slot2.markDirty();
							bl = true;
						} else if (itemStack.getCount() < stack.getMaxCount()) {
							stack.decrement(stack.getMaxCount() - itemStack.getCount());
							itemStack.setCount(stack.getMaxCount());
							slot2.markDirty();
							bl = true;
						}
					}
				}
				if (fromLast) {
					--i;
				} else {
					++i;
				}
			}
		}

		if (!stack.isEmpty()) {
			if (fromLast) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while(true) {
				Before:
				{
					if (fromLast) {
						if (i < startIndex) {
							break;
						}
					} else if (i >= endIndex) {
						break;
					}

					slot2 = (Slot)this.slots.get(i);

					if (slot2 instanceof ActionItemSlot) {
						break Before;
					}

					itemStack = slot2.getStack();
					if (itemStack.isEmpty() && slot2.canInsert(stack)) {
						if (stack.getCount() > slot2.getMaxItemCount()) {
							slot2.setStack(stack.split(slot2.getMaxItemCount()));
						} else {
							slot2.setStack(stack.split(stack.getCount()));
						}

						slot2.markDirty();
						bl = true;
						break;
					}
				}
				if (fromLast) {
					--i;
				} else {
					++i;
				}
			}
		}

		return bl;
	}

	@Override
	public boolean canInsertIntoSlot(Slot slot) {
		return !(slot instanceof ActionItemSlot);
	}

	private static ScreenHandlerType<?> getType(int rows) {
		switch (rows) {
		case 1:
			return ScreenHandlerType.GENERIC_9X1;
		case 2:
			return ScreenHandlerType.GENERIC_9X2;
		case 3:
			return ScreenHandlerType.GENERIC_9X3;
		case 4:
			return ScreenHandlerType.GENERIC_9X4;
		case 5:
			return ScreenHandlerType.GENERIC_9X5;
		case 6:
			return ScreenHandlerType.GENERIC_9X6;
		default:
			return null;
		}
	}
}