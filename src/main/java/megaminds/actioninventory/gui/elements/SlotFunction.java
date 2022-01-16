package megaminds.actioninventory.gui.elements;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.misc.Enums.GuiType;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Poly;
import megaminds.actioninventory.gui.NamedGui;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Poly("Redirect")
public non-sealed class SlotFunction implements Validated, SlotElement {	
	private int index;
	private GuiType type;
	private String name;
	private int slotIndex;

	public Slot getSlot(ServerPlayerEntity p) {
		ServerPlayerEntity real = name==null ? p : p.getServer().getPlayerManager().getPlayer(UUID.fromString(name));
		return switch (type) {
		case PLAYER -> new Slot(real.getInventory(), slotIndex, 0, 0);
		case ENDER_CHEST -> new Slot(real.getEnderChestInventory(), slotIndex, 0, 0);
		default -> throw new IllegalArgumentException("Unimplemented case: " + type);
		};
	}

	@Override
	public void validate() {
		if (type==null) type = GuiType.PLAYER;
		Validated.validate(slotIndex>=0, "Redirect requires slotIndex to be 0 or greater");
	}

	@Override
	public void apply(NamedGui gui, ServerPlayerEntity p) {
		gui.setSlotRedirect(getCheckedIndex(gui), getSlot(p));
	}
}