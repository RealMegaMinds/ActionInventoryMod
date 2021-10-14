package megaminds.testmod.inventory.actions.messaging;

import java.util.List;
import java.util.UUID;

import megaminds.testmod.MessageHelper;
import megaminds.testmod.inventory.actions.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

/**
 * Sends a message to multiple players
 */
public class ToMultiMessageAction implements Action {
	private final MutableText msg;
	private final List<UUID> to;
	private final boolean fromServer;
	
	/**
	 * @param msg
	 * The message to send
	 * @param to
	 * The users to send the message to
	 * @param fromServer
	 * True - send a system message
	 * False - the player is the sender
	 */
	public ToMultiMessageAction(MutableText msg, List<UUID> to, boolean fromServer) {
		this.msg = msg;
		this.to = to;
		this.fromServer = fromServer;
	}

	@Override
	public void execute(ServerPlayerEntity player) {
		MessageHelper.multiMessage(player, msg, to, fromServer);
	}
}