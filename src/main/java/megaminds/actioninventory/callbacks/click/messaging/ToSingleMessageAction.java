package megaminds.actioninventory.callbacks.click.messaging;

import java.util.UUID;

import megaminds.actioninventory.callbacks.click.Action;
import megaminds.actioninventory.util.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

/**
 * Sends a message to a single player
 */
public class ToSingleMessageAction implements Action {
	/**The message to send*/
	private MutableText msg;
	/**The player to send the message to*/
	private UUID to;
	/**True - send a system message
	 * False - the player is the sender*/
	private boolean fromServer;
	
	@Override
	public void execute(ServerPlayerEntity player) {
		MessageHelper.singleMessage(player, msg, to, fromServer);
	}
}