package megaminds.actioninventory.actions;

import java.util.UUID;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.misc.Enums.GuiType;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.MessageHelper;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public final class OpenGui extends BasicAction {
	private GuiType guiType;
	private Identifier guiName;
	private UUID playerUUID;

	public OpenGui() {}
	
	public OpenGui(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, GuiType guiType, Identifier guiName, UUID playerUUID) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
		this.guiName = guiName;
		this.guiType = guiType;
		this.playerUUID = playerUUID;
	}

	public OpenGui(GuiType guiType, Identifier guiName) {
		this.guiType = guiType;
		this.guiName = guiName;
	}

	public OpenGui(GuiType guiType, UUID playerUUID) {
		this.guiType = guiType;
		this.playerUUID = playerUUID;
	}

	@Override
	public void accept(ActionInventoryGui gui) {
		if (guiType==null) {
			gui.close();
			return;
		}

		var player = gui.getPlayer();
		switch (guiType) {
		case ENDER_CHEST -> {
			var uuid = playerUUID!=null ? playerUUID : player.getUuid();
			ActionInventoryMod.INVENTORY_LOADER.openEnderChest(player, uuid);
		}
		case NAMED_GUI -> {
			var b = ActionInventoryMod.INVENTORY_LOADER.getBuilder(guiName);
			if (b==null) {
				gui.close();
				player.sendMessage(MessageHelper.toError("No action inventory of name: "+guiName));
			} else {
				b.build(player).open(gui);
			}
		}
		case PLAYER -> {
			var uuid = playerUUID!=null ? playerUUID : player.getUuid();
			ActionInventoryMod.INVENTORY_LOADER.openInventory(player, uuid);
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

	@Override
	public BasicAction copy() {
		var copy = new OpenGui(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), guiType, guiName, playerUUID);
		copy.playerUUID = playerUUID;
		return copy;
	}

	public GuiType getGuiType() {
		return guiType;
	}

	public void setGuiType(GuiType guiType) {
		this.guiType = guiType;
	}

	public Identifier getGuiName() {
		return guiName;
	}

	public void setGuiName(Identifier guiName) {
		this.guiName = guiName;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public void setPlayerUUID(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}
}