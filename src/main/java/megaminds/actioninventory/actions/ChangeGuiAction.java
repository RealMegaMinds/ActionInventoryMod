package megaminds.actioninventory.actions;

import java.util.UUID;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.loaders.NamedGuiLoader;
import megaminds.actioninventory.misc.Constants.GuiType;
import megaminds.actioninventory.util.annotations.TypeName;
import megaminds.actioninventory.util.Helper;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@TypeName("OpenGui")
public final class ChangeGuiAction extends BasicAction {
	private Identifier guiName;
	private String playerUUID;
	private GuiType guiType;
	
	private ChangeGuiAction() {}

	@Override
	public void internalClick(int index, ClickType cType, SlotActionType action, NamedSlotGuiInterface gui) {
		if (guiType==null) {
			gui.close();
			return;
		}
		
		switch (guiType) {
		case ENDER_CHEST -> {
			UUID uuid = Helper.apply(playerUUID, UUID::fromString, gui.getPlayer()::getUuid);
			NamedGuiLoader.openEnderChest(gui.getPlayer(), uuid);
		}
		case NAMED_GUI -> NamedGuiLoader.openGui(gui.getPlayer(), guiName);
		case PLAYER -> {
			UUID uuid = Helper.apply(playerUUID, UUID::fromString, gui.getPlayer()::getUuid);
			NamedGuiLoader.openInventory(gui.getPlayer(), uuid);
		}
		default -> throw new IllegalArgumentException("Unimplemented case: " + guiType);
		}
	}
}