package megaminds.actioninventory.actions;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.MessageHelper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

/**
 * This executes a command.
 * Some other actions may be a better option for doing certain things.
 * @see ToPlayerMessageAction
 * @see ToAllMessageAction
 * @see GiveAction
 * @see OpenActionInventoryAction
 */
@PolyName("Command")
public final class CommandAction extends BasicAction {
	private String command;
	private TriState fromServer = TriState.DEFAULT;
	/**@since 3.1*/
	private TriState silent = TriState.DEFAULT;
	/**@since 3.1*/
	private Integer higherLevel;

	public CommandAction() {}
	
	public CommandAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe, Identifier requiredGuiName, String command, TriState fromServer, TriState silent, Integer higherLevel) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
		this.command = command;
		this.fromServer = fromServer;
		this.silent = silent;
		this.higherLevel = higherLevel;
	}

	public CommandAction(String command, TriState fromServer, TriState silent, Integer higherLevel) {
		this.command = command;
		this.fromServer = fromServer;
		this.silent = silent;
		this.higherLevel = higherLevel;
	}
	
	@Override
	public void accept(@NotNull ActionInventoryGui gui) {
		var player = gui.getPlayer();

		var source = fromServer.orElse(false) ? player.getServer().getCommandSource() : player.getCommandSource();
		if (silent.orElse(false)) source = source.withSilent();
		if (higherLevel!=null) source = source.withMaxLevel(higherLevel);

		MessageHelper.executeCommand(source, command);
	}

	@Override
	public void validate() {
		if (command==null) command = "";
	}

	@Override
	public BasicAction copy() {
		return new CommandAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), command, fromServer, silent, higherLevel);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public TriState getFromServer() {
		return fromServer;
	}

	public void setFromServer(TriState fromServer) {
		this.fromServer = fromServer;
	}

	public TriState getSilent() {
		return silent;
	}

	public void setSilent(TriState silent) {
		this.silent = silent;
	}

	public Integer getHigherLevel() {
		return higherLevel;
	}

	public void setHigherLevel(Integer higherLevel) {
		this.higherLevel = higherLevel;
	}
}