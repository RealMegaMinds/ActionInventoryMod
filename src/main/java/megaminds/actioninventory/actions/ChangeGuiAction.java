package megaminds.actioninventory.actions;

import java.util.UUID;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.loaders.NamedGuiLoader;
import megaminds.actioninventory.misc.Constants.GuiType;
import megaminds.actioninventory.misc.Validated;
import megaminds.actioninventory.util.annotations.TypeName;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@TypeName("OpenGui")
public final class ChangeGuiAction extends BasicAction {
	private Identifier guiName;
	private UUID playerUUID;
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
			UUID uuid = playerUUID!=null ? playerUUID : gui.getPlayer().getUuid();
			NamedGuiLoader.openEnderChest(gui.getPlayer(), uuid);
		}
		case NAMED_GUI -> NamedGuiLoader.openGui(gui.getPlayer(), guiName);
		case PLAYER -> {
			UUID uuid = playerUUID!=null ? playerUUID : gui.getPlayer().getUuid();
			NamedGuiLoader.openInventory(gui.getPlayer(), uuid);
		}
		default -> throw new IllegalArgumentException("Unimplemented case: " + guiType);
		}
	}

	@Override
	public void validate() {
		if (guiType==null) return;
		switch(guiType) {
		case ENDER_CHEST, PLAYER -> guiName = null;
		case NAMED_GUI -> {
			Validated.validate(guiName!=null, "guiName cannot be null.");
			playerUUID = null;
		}
		default-> throw new IllegalArgumentException("OpenGui action doesn't support guiType of: "+guiType);
		}
	}
}