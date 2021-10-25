package megaminds.testmod.inventory.actions.messaging;

import megaminds.testmod.MessageHelper;
import megaminds.testmod.inventory.actions.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

/**
 * Sends a message to the player that activated this action
 */
public class ToPlayerMessageAction extends Action {
	/**The message to send*/
	private MutableText msg;
	/**True - the message is from the server
	 * False - the message is from nil*/
	private boolean fromServer;
	
	@Override
	public void execute(ServerPlayerEntity player) {
		MessageHelper.toPlayerMessage(player, msg, fromServer);
	}

	@Override
	protected Type getTypeInternal() {
		return Type.PlayerMessage;
	}
}