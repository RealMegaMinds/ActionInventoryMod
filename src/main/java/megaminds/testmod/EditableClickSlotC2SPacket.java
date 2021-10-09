package megaminds.testmod;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

public class EditableClickSlotC2SPacket {
	public int syncId, revision, slot, button;
	public SlotActionType actionType;
	public ItemStack stack;
	public Int2ObjectMap<ItemStack> modifiedStacks;
	
	public EditableClickSlotC2SPacket(ClickSlotC2SPacket packet) {
		syncId = packet.getSyncId();
		revision = packet.getRevision();
		slot = packet.getSlot();
		button = packet.getButton();
		actionType = packet.getActionType();
		stack = packet.getStack();
		modifiedStacks = packet.getModifiedStacks();
	}
	
	public EditableClickSlotC2SPacket syncId(int syncId) {
		this.syncId = syncId;
		return this;
	}
	
	public EditableClickSlotC2SPacket revision(int revision) {
		this.revision = revision;
		return this;
	}
	
	public EditableClickSlotC2SPacket slot(int slot) {
		this.slot = slot;
		return this;
	}
	
	public EditableClickSlotC2SPacket button(int button) {
		this.button = button;
		return this;
	}
	
	public EditableClickSlotC2SPacket actionType(SlotActionType actionType) {
		this.actionType = actionType;
		return this;
	}
	
	public EditableClickSlotC2SPacket stack(ItemStack stack) {
		this.stack = stack;
		return this;
	}
	
	public EditableClickSlotC2SPacket modifiedStacks(Int2ObjectMap<ItemStack> modifiedStacks) {
		this.modifiedStacks = modifiedStacks;
		return this;
	}
	
	public ClickSlotC2SPacket asPacket() {
		return new ClickSlotC2SPacket(syncId, revision, slot, button, actionType, stack, modifiedStacks);
	}
}