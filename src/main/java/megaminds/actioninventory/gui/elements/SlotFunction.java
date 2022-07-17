package megaminds.actioninventory.gui.elements;

import java.util.UUID;

import megaminds.actioninventory.misc.Enums.GuiType;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.PolyName;
import megaminds.actioninventory.gui.ActionInventoryGui;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

@PolyName("Redirect")
public non-sealed class SlotFunction extends SlotElement implements Validated {	
	private GuiType guiType;
	private UUID name;
	private int slotIndex;
	
	public SlotFunction() {}
	
	public SlotFunction(int index, GuiType guiType, UUID name, int slotIndex) {
		super(index);
		this.guiType = guiType;
		this.name = name;
		this.slotIndex = slotIndex;
	}

	public Slot getSlot(ServerPlayerEntity p) {
		ServerPlayerEntity real = name==null ? p : p.getServer().getPlayerManager().getPlayer(name);
		return switch (guiType) {
		case PLAYER -> new Slot(real.getInventory(), slotIndex, 0, 0);
		case ENDER_CHEST -> new Slot(real.getEnderChestInventory(), slotIndex, 0, 0);
		default -> throw new IllegalArgumentException("Unimplemented case: " + guiType);
		};
	}

	@Override
	public void validate() {
		if (guiType==null) guiType = GuiType.PLAYER;
		Validated.validate(slotIndex>=0, "Redirect requires slotIndex to be 0 or greater");
	}

	@Override
	public void apply(ActionInventoryGui gui, ServerPlayerEntity p) {
		gui.setSlotRedirect(getCheckedIndex(gui), getSlot(p));
	}

	@Override
	public SlotElement copy() {
		SlotFunction copy = new SlotFunction();
		copy.guiType = guiType;
		copy.name = name;
		copy.slotIndex = slotIndex;
		return copy;
	}

	public GuiType getGuiType() {
		return guiType;
	}

	public void setGuiType(GuiType guiType) {
		this.guiType = guiType;
	}

	public UUID getName() {
		return name;
	}

	public void setName(UUID name) {
		this.name = name;
	}

	public int getSlotIndex() {
		return slotIndex;
	}

	public void setSlotIndex(int slotIndex) {
		this.slotIndex = slotIndex;
	}
}