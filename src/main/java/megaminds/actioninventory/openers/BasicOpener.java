package megaminds.actioninventory.openers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.gui.ActionInventoryBuilder;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.MessageHelper;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Poly
public abstract sealed class BasicOpener implements Validated permits BlockOpener, EntityOpener, ItemOpener {
	private Identifier guiName;
	
	/**
	 * Returns true if the action inventory should have opened; this may not be the case if the builder for it doesn't exist.
	 */
	public boolean open(ServerPlayerEntity player, Object... context) {	//NOSONAR Used by subclasses
		var b = ActionInventoryMod.INVENTORY_LOADER.getBuilder(guiName);
		if (b==null) {
			player.sendSystemMessage(MessageHelper.toError("No action inventory of name: "+guiName), Util.NIL_UUID);
		} else {
			b.build(player).open();
		}
		return true;
	}
	
	@Override
	public void validate() {
		Validated.validate(guiName!=null, "Openers require guiName to not be null.");
	}
	
	public abstract Identifier getType();
}