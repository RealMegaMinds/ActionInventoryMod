package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PolyName("Command")
public final class CommandAction extends BasicAction {
	private String command;
	private TriState fromServer = TriState.DEFAULT;
	/**@since 3.1*/
	private TriState silent = TriState.DEFAULT;
	/**@since 3.1*/
	private Integer higherLevel;

	public CommandAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe, Identifier requiredGuiName, String command, TriState fromServer, TriState silent, Integer higherLevel) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
		this.command = command;
		this.fromServer = fromServer;
		this.silent = silent;
		this.higherLevel = higherLevel;
	}

	@Override
	public void accept(ActionInventoryGui gui) {
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
}