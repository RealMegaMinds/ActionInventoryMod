package megaminds.testmod.inventory.actions.messaging;

import megaminds.testmod.MessageHelper;
import megaminds.testmod.inventory.actions.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

/**
 * Sends a message to the player that activated this action
 */
public class ToPlayerMessageAction implements Action {
	private final MutableText msg;
	private final boolean fromServer;
	
	/**
	 * @param msg
	 * The message to send
	 * @param fromServer
	 * True - the message is from the server
	 * False - the message is from nil
	 */
	public ToPlayerMessageAction(MutableText msg, boolean fromServer) {
		this.msg = msg;
		this.fromServer = fromServer;
	}

	@Override
	public void execute(ServerPlayerEntity player) {
		MessageHelper.toPlayerMessage(player, msg, fromServer);
	}
}