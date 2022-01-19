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
import net.minecraft.server.command.ServerCommandSource;
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
	private boolean silent;
	private Integer higherLevel;
	
	public CommandAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Identifier requiredGuiName, String command, boolean fromServer, boolean silent, Integer higherLevel) {
		super(requiredIndex, clicktype, actionType, requiredGuiName);
		this.command = command;
		this.fromServer = fromServer;
		this.silent = silent;
		this.higherLevel = higherLevel;
	}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		
		ServerCommandSource source = fromServer ? player.getServer().getCommandSource() : player.getCommandSource();
		if (silent) source = source.withSilent();
		if (higherLevel!=null) source = source.withMaxLevel(higherLevel);
		
		MessageHelper.executeCommand(source, command);
	}

	@Override
	public void validate() {
		if (command==null) command = "";
	}

	@Override
	public BasicAction copy() {
		return new CommandAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequiredGuiName(), command, fromServer, silent, higherLevel);
	}
}