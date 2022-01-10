package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.MessageHelper;
import megaminds.actioninventory.util.TypeName;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This executes a command.
 * Some other actions may be a better option for doing certain things.
 * @see ToPlayerMessageAction
 * @see ToAllMessageAction
 * @see GiveAction
 * @see OpenActionInventoryAction
 */
@TypeName("Command")
public final class CommandAction extends BasicAction {
	private String command;
	private boolean fromServer;
	private boolean makeTempOp;
	
	public CommandAction() {
		command = "";
	}
			
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();

		if (fromServer) {
			MessageHelper.executeCommand(player.getServer(), command);
		} else if (makeTempOp) {
			MessageHelper.executeOppedCommand(player, command);
		} else {
			MessageHelper.executeCommand(player, command);
		}
	}
}