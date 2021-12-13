package megaminds.actioninventory.inventory.actions.messaging;

import megaminds.actioninventory.inventory.actions.Action;
import megaminds.actioninventory.util.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

/**
 * Sends a message to the player that activated this action
 */
public class ToPlayerMessageAction implements Action {
	/**The message to send*/
	private MutableText msg;
	/**True - the message is from the server
	 * False - the message is from nil*/
	private boolean fromServer;
	
	@Override
	public void execute(ServerPlayerEntity player) {
		MessageHelper.toPlayerMessage(player, msg, fromServer);
	}
}