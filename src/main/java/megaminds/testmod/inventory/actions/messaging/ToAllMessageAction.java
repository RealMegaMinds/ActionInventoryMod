package megaminds.testmod.inventory.actions.messaging;

import megaminds.testmod.MessageHelper;
import megaminds.testmod.inventory.actions.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

/**
 * Broadcasts a message to all players
 */
public class ToAllMessageAction extends Action {
	/**The message to send*/
	private MutableText msg;
	/**True - send a system message<br>
	 * False - the player is the sender*/
	private boolean fromServer;

	@Override
	public void execute(ServerPlayerEntity player) {
		MessageHelper.broadcastMessage(player, msg, fromServer);
	}

	@Override
	protected Type getTypeInternal() {
		return Type.AllMessage;
	}
}