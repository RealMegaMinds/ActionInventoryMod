package megaminds.actioninventory.callbacks.click;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.util.MessageHelper;
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
public class CommandAction extends BasicAction {
	private static final String COMMAND = "command", FROM_SERVER = "fromServer", MAKE_OP = "makeTempOp";
	
	private String command;
	private boolean fromServer;
	private boolean makeTempOp;
		
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
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
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		this.command = obj.has("command") ? obj.get(COMMAND).getAsString() : "";
		this.fromServer = obj.has(FROM_SERVER) ? obj.get(FROM_SERVER).getAsBoolean() : false;
		this.makeTempOp = obj.has(MAKE_OP) ? obj.get(MAKE_OP).getAsBoolean() : false;
		return this;
	}
}