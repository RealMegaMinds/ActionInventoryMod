package megaminds.actioninventory.gui.input;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.virtual.inventory.VirtualScreenHandler;
import eu.pb4.sgui.virtual.inventory.VirtualSlot;
import megaminds.actioninventory.gui.BetterGui;
import megaminds.actioninventory.gui.BetterGuiI;
import megaminds.actioninventory.gui.callback.BetterClickCallback;
import megaminds.actioninventory.misc.ItemStackArgs;
import megaminds.actioninventory.util.ElementHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemStackInput extends InputCallback<ItemStackArgs> {
	@Override
	public InputCallback<ItemStackArgs> defaultAnswer(ItemStackArgs defaultAnswer) {
		this.defaultAnswer = Objects.requireNonNullElse(defaultAnswer, ItemStackArgs.EMPTY);
		return this;
	}
	
	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		BetterGuiI bg = new BetterGui(ScreenHandlerType.GENERIC_9X2, gui.getPlayer(), false);
		bg.setChained(true);
		bg.setTitle(question);
		
		final ItemStackArgs stack = defaultArgs.copy();
		bg.setSlot(0, ElementHelper.getConfirm((i,t,a,g)->onFinish.accept(stack)));
		bg.setSlot(1, ElementHelper.getCancel(null));
		bg.setSlotRedirect(2, new DuplicatingSlot(stack::copyFrom));
		
		bg.setSlot(5, null);//item
		bg.setSlot(6, Items.STICK.getDefaultStack(), new IntInput().question(new LiteralText("How many items?")).defaultAnswer("1").boundedPostFilter(1, 64));//count
		bg.setSlot(7, Items.DAMAGED_ANVIL.getDefaultStack(), new IntInput().question(new LiteralText("Damaged by how much?")).defaultAnswer("0").boundedPostFilter(1, Integer.MAX_VALUE));//damage
		bg.setSlot(8, null);//customNbt
		bg.setSlot(9, null);//name
		bg.setSlot(10, null);//lore
		bg.setSlot(11, null);//color
		bg.setSlot(12, null);//enchants
		bg.setSlot(13, null);//hide flags
		bg.setSlot(14, null);//attributes
		
		
		
		return false;
	}
	
	private class DuplicatingSlot extends Slot {
		private Consumer<ItemStack> onClick;
		
		public DuplicatingSlot(Consumer<ItemStack> onClick) {
			super(null, 0, 0, 0);
			this.onClick = Objects.requireNonNullElse(onClick, s->{});
		}

		@Override
		public ItemStack insertStack(ItemStack stack, int count) {
			onClick.accept(stack);
			return stack;
		}
		@Override public ItemStack getStack() {return ItemStack.EMPTY;}
		@Override public void setStack(ItemStack stack) {}
		@Override public void markDirty() {}
		@Override public int getMaxItemCount() {return Integer.MAX_VALUE;}
		@Override public ItemStack takeStack(int amount) {return ItemStack.EMPTY;}
	}
}