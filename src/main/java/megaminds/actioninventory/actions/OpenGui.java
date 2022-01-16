package megaminds.actioninventory.actions;

import java.util.UUID;

import eu.pb4.sgui.api.ClickType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.loaders.NamedGuiLoader;
import megaminds.actioninventory.misc.Enums.GuiType;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@NoArgsConstructor
@Getter
@Setter
public final class OpenGui extends BasicAction {
	private GuiType guiType;
	private Identifier guiName;
	private UUID playerUUID;
	
	public OpenGui(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, String requiredGuiName, GuiType guiType, Identifier guiName) {
		super(requiredIndex, clicktype, actionType, requiredGuiName);
		this.guiName = guiName;
		this.guiType = guiType;
	}
	
	public OpenGui(GuiType guiType, Identifier guiName) {
		this.guiType = guiType;
		this.guiName = guiName;
	}

	public OpenGui(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, String requiredGuiName, GuiType guiType, UUID playerUUID) {
		super(requiredIndex, clicktype, actionType, requiredGuiName);
		this.playerUUID = playerUUID;
		this.guiType = guiType;
	}

	public OpenGui(GuiType guiType, UUID playerUUID) {
		this.guiType = guiType;
		this.playerUUID = playerUUID;
	}

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
		case ENDER_CHEST, PLAYER -> guiName = null; //NOSONAR
		case NAMED_GUI -> {
			Validated.validate(guiName!=null, "guiName cannot be null.");
			playerUUID = null;
		}
		default-> throw new IllegalArgumentException("OpenGui action doesn't support guiType of: "+guiType);
		}
	}
}