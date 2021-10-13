package megaminds.testmod.actions.messaging;

import megaminds.testmod.Helper;
import megaminds.testmod.actions.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

/**
 * Broadcasts a message to all players
 */
public class ToAllMessageAction implements Action {
	private final MutableText msg;
	private final boolean fromServer;

	/**
	 * @param msg
	 * The message to send
	 * @param fromServer
	 * True - send a system message
	 * False - the player is the sender
	 */
	public ToAllMessageAction(MutableText msg, boolean fromServer) {
		this.msg = msg;
		this.fromServer = fromServer;
	}

	@Override
	public void execute(ServerPlayerEntity player) {
		Helper.broadcastMessage(player, msg, fromServer);
	}
}