package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class VirtualPlayerInventory extends SimpleGui {
	public VirtualPlayerInventory(ServerPlayerEntity player, boolean includePlayerInventorySlots, ServerPlayerEntity toLookAt) {
		super(ScreenHandlerType.GENERIC_9X6, player, includePlayerInventorySlots);
		PlayerInventory inv = toLookAt.getInventory();
		setTitle(toLookAt.getName().shallowCopy().append(new LiteralText("'s Inventory")));

		GuiElementBuilder builder = new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE);
		int i = 0;
		for (; i < PlayerInventory.MAIN_SIZE-PlayerInventory.getHotbarSize(); i++) {
			this.setSlotRedirect(i, new Slot(inv, i+PlayerInventory.getHotbarSize(), 0, 0));
		}
		for (int j = 0; j < PlayerInventory.getHotbarSize(); j++, i++) {
			this.setSlotRedirect(i, new Slot(inv, j, 0, 0));
		}
		this.setSlot(i, builder.setName(new LiteralText("Armor")).build());
		i++;
		for (int j = PlayerInventory.MAIN_SIZE+PlayerInventory.ARMOR_SLOTS.length-1, k = 0; j>=PlayerInventory.MAIN_SIZE; j--, k++, i++) {
			this.setSlotRedirect(i, new ArmorSlot(inv, j, k));
		}
		this.setSlot(i, builder.setName(new LiteralText("Offhand")).build());
		i++;
		this.setSlotRedirect(i, new Slot(inv, PlayerInventory.OFF_HAND_SLOT, 0, 0));
		i++;
		builder.setName(LiteralText.EMPTY);
		for(; i < this.getSize(); i++) {
			this.setSlot(i, builder.build());
		}
	}

	private class ArmorSlot extends Slot {
		private EquipmentSlot equipmentSlot;
		ArmorSlot(Inventory inv, int index, int armorIndex) {
			super(inv, index, 0, 0);
			this.equipmentSlot = EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, 3-armorIndex);
		}
		@Override
		public int getMaxItemCount() {
			return 1;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return equipmentSlot == LivingEntity.getPreferredEquipmentSlot(stack);
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			ItemStack itemStack = this.getStack();
			if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
				return false;
			}
			return super.canTakeItems(playerEntity);
		}
	}
}