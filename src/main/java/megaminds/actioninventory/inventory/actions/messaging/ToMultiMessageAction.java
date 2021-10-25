package megaminds.actioninventory.inventory.actions.messaging;

import java.util.List;
import java.util.UUID;

import megaminds.actioninventory.MessageHelper;
import megaminds.actioninventory.inventory.actions.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

/**
 * Sends a message to multiple players
 */
public class ToMultiMessageAction extends Action {
	/**The message to send*/
	private MutableText msg;
	/**The users to send the message to*/
	private List<UUID> to;
	/**True - send a system message<br>
	 * False - the player is the sender*/
	private boolean fromServer;
	
	@Override
	public void execute(ServerPlayerEntity player) {
		MessageHelper.multiMessage(player, msg, to, fromServer);
	}

	@Override
	protected Type getTypeInternal() {
		return Type.MultiMessage;
	}
}