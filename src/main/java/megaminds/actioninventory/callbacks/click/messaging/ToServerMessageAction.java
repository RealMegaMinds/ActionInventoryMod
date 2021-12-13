package megaminds.actioninventory.callbacks.click.messaging;

import megaminds.actioninventory.callbacks.click.Action;
import megaminds.actioninventory.util.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Logs a message to the server
 */
public class ToServerMessageAction implements Action {
	/**The message to send*/
	private Text message;
	/**True - the message is from the player
	 * False - the message is from nil*/
	private boolean fromPlayer;
	
	@Override
	public void execute(ServerPlayerEntity player) {
		MessageHelper.logMessage(player, message, fromPlayer);
	}
}