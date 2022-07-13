package megaminds.actioninventory.actions;

import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.ActionInventoryMod;
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
@Getter
public class CommandAction extends ClickAwareAction {
	private final String command;
	private final TriState fromServer;
	/**@since 3.1*/
	private final TriState silent;
	/**@since 3.1*/
	private final Integer higherLevel;

	public CommandAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Boolean requireShift, Identifier requiredRecipe, Identifier requiredGuiName, String command, TriState fromServer, TriState silent, Integer higherLevel) {
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

		MessageHelper.executeCommand(source, Objects.requireNonNullElse(command, ""));
	}

	@Override
	public Identifier getType() {
		return new Identifier(ActionInventoryMod.MOD_ID, "Command");
	}

	@Override
	public void validate(@NotNull Consumer<String> errorReporter) { /* Nothing to validate */ }
}