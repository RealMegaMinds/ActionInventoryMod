package megaminds.actioninventory.gui.elements;

import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.misc.Enums.GuiType;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.PolyName;
import megaminds.actioninventory.gui.ActionInventoryGui;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@PolyName("Redirect")
public non-sealed class SlotFunction extends SlotElement implements Validated {	
	private GuiType guiType;
	private UUID name;
	private int slotIndex;

	public SlotFunction(int index, GuiType guiType, UUID name, int slotIndex) {
		super(index);
		this.guiType = guiType;
		this.name = name;
		this.slotIndex = slotIndex;
	}

	@Override
	public void validate(@NotNull Consumer<String> errorReporter) {
		if (guiType==null) guiType = GuiType.PLAYER;
		if (slotIndex<0) errorReporter.accept("slotIndex is: "+slotIndex+", but must be >= 0");
	}

	public Slot getSlot(ServerPlayerEntity p) {
		ServerPlayerEntity real = name==null ? p : Helper.getPlayer(p.getServer(), name);
		return switch (guiType) {
			case PLAYER -> new Slot(real.getInventory(), slotIndex, 0, 0);
			case ENDER_CHEST -> new Slot(real.getEnderChestInventory(), slotIndex, 0, 0);
			default -> throw new IllegalArgumentException("Unimplemented case: " + guiType);
		};
	}

	@Override
	public void apply(ActionInventoryGui gui, ServerPlayerEntity p) {
		gui.setSlotRedirect(getCheckedIndex(gui), getSlot(p));
	}

	@Override
	public SlotElement copy() {
		return new SlotFunction(getIndex(), guiType, name, slotIndex);
	}
}