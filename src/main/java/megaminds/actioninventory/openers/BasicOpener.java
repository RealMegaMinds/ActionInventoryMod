package megaminds.actioninventory.openers;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.MessageHelper;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@Poly
public abstract sealed class BasicOpener implements Validated permits BlockOpener, EntityOpener, ItemOpener {
	private Identifier guiName;

	protected BasicOpener() {}
	protected BasicOpener(Identifier guiName) {
		this.guiName = guiName;
	}

	/**
	 * Returns true if the action inventory should have opened; this may not be the case if the builder for it doesn't exist.
	 */
	@SuppressWarnings("unused") //Used by subclasses
	public boolean open(ServerPlayerEntity player, Object... context) {
		var b = ActionInventoryMod.INVENTORY_LOADER.getBuilder(guiName);
		if (b==null) {
			player.sendMessage(MessageHelper.toError("No action inventory of name: "+guiName));
		} else {
			b.build(player).open();
		}
		return true;
	}

	@Override
	public void validate() {
		Validated.validate(guiName!=null, "Openers require guiName to not be null.");
	}

	public Identifier getGuiName() {
		return guiName;
	}

	public void setGuiName(Identifier guiName) {
		this.guiName = guiName;
	}

	public abstract Identifier getType();
}