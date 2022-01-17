package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.MessageHelper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * This executes a command.
 * Some other actions may be a better option for doing certain things.
 * @see ToPlayerMessageAction
 * @see ToAllMessageAction
 * @see GiveAction
 * @see OpenActionInventoryAction
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PolyName("Command")
public final class CommandAction extends BasicAction {
	private String command;
	private boolean fromServer;
	private boolean makeTempOp;
	
	public CommandAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Identifier requiredGuiName, String command, boolean fromServer, boolean makeTempOp) {
		super(requiredIndex, clicktype, actionType, requiredGuiName);
		this.command = command;
		this.fromServer = fromServer;
		this.makeTempOp = makeTempOp;
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

	@Override
	public void validate() {
		if (command==null) command = "";
	}

	@Override
	public BasicAction copy() {
		return new CommandAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequiredGuiName(), command, fromServer, makeTempOp);
	}
}