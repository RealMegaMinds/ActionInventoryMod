package megaminds.testmod.actions.messaging;

import megaminds.testmod.Helper;
import megaminds.testmod.actions.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Logs a message to the server
 */
public class ToServerMessageAction implements Action {
	private final Text message;
	private final boolean fromPlayer;
	
	/**
	 * @param message
	 * The message to send
	 * @param fromPlayer
	 * True - the message is from the player
	 * False - the message is from nil
	 */
	public ToServerMessageAction(Text message, boolean fromPlayer) {
		this.message = message;
		this.fromPlayer = fromPlayer;
	}
	
	@Override
	public void execute(ServerPlayerEntity player) {
		Helper.logMessage(player, message, fromPlayer);
	}
}