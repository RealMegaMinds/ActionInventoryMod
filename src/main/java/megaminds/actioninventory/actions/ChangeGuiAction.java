package megaminds.actioninventory.actions;

import java.util.UUID;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.Constants.GuiType;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.NamedGuiLoader;
import megaminds.actioninventory.util.TypeName;
import net.minecraft.screen.slot.SlotActionType;

@TypeName("OpenGui")
public final class ChangeGuiAction extends BasicAction {
	private String name;
	private GuiType guiType;
	
	public ChangeGuiAction() {
	}

	@Override
	public void internalClick(int index, ClickType cType, SlotActionType action, NamedSlotGuiInterface gui) {
		if (guiType==null) gui.close();
		
		switch (guiType) {
		case ENDER_CHEST -> {
			UUID uuid = Helper.apply(name, UUID::fromString, gui.getPlayer()::getUuid);
			NamedGuiLoader.openEnderChest(gui.getPlayer(), uuid);
		}
		case NAMED_GUI -> {
			NamedGuiLoader.openGui(gui.getPlayer(), name);
		}
		case PLAYER -> {
			UUID uuid = Helper.apply(name, UUID::fromString, gui.getPlayer()::getUuid);
			NamedGuiLoader.openInventory(gui.getPlayer(), uuid);
		}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GuiType getGuiType() {
		return guiType;
	}

	public void setGuiType(GuiType guiType) {
		this.guiType = guiType;
	}
}