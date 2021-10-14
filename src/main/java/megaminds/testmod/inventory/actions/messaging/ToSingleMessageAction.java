package megaminds.testmod.inventory.actions.messaging;

import java.util.UUID;

import megaminds.testmod.MessageHelper;
import megaminds.testmod.inventory.actions.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

/**
 * Sends a message to a single player
 */
public class ToSingleMessageAction implements Action {
	private final MutableText msg;
	private final UUID to;
	private final boolean fromServer;
	
	/**
	 * @param msg
	 * The message to send
	 * @param to
	 * The player to send the message to
	 * @param fromServer
	 * True - send a system message
	 * False - the player is the sender
	 */
	public ToSingleMessageAction(MutableText msg, UUID to, boolean fromServer) {
		this.msg = msg;
		this.to = to;
		this.fromServer = fromServer;
	}

	@Override
	public void execute(ServerPlayerEntity player) {
		MessageHelper.singleMessage(player, msg, to, fromServer);
	}
}