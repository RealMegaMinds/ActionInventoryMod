package megaminds.actioninventory.gui;

import java.util.UUID;

import megaminds.actioninventory.misc.Constants.GuiType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class SlotFunction {	
	private GuiType type;
	private String name;
	private int index;
	
	public SlotFunction(GuiType type, String name, int index) {
		this.type = type;
		this.name = name;
		this.index = index;
	}
	
	public static SlotFunction build(Slot slot) {
		return new SlotFunction(GuiType.GENERATED, "", slot.getIndex()) {
			@Override public Slot getSlot(ServerPlayerEntity p) {return slot;}
		};
	}

	public Slot getSlot(ServerPlayerEntity p) {
		ServerPlayerEntity real = name==null ? p : p.getServer().getPlayerManager().getPlayer(UUID.fromString(name));
		return switch (type) {
		case PLAYER -> new Slot(real.getInventory(), index, 0, 0);
		case ENDER_CHEST -> new Slot(real.getEnderChestInventory(), index, 0, 0);
		default -> throw new IllegalArgumentException("Unimplemented case: " + type);
		};
	}

	public GuiType getType() {
		return type;
	}

	public void setType(GuiType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}