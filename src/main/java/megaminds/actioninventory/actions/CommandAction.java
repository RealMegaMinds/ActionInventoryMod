package megaminds.actioninventory.actions;

import static megaminds.actioninventory.util.JsonHelper.*;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
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
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		this.command = string(obj.get(COMMAND), "");
		this.fromServer = bool(obj.get(FROM_SERVER));
		this.makeTempOp = bool(obj.get(MAKE_OP));
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		obj.addProperty(COMMAND, command);
		obj.addProperty(FROM_SERVER, fromServer);
		obj.addProperty(MAKE_OP, makeTempOp);
		return obj;
	}

	@Override
	public Action getType() {
		return Action.COMMAND;
	}
}