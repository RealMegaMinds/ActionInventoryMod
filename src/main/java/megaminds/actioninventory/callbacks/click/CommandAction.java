package megaminds.actioninventory.callbacks.click;

import com.mojang.authlib.GameProfile;

import megaminds.actioninventory.util.MessageHelper;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This executes a command.
 * Some other actions may be a better option for doing certain things.
 * @see ToPlayerMessageAction
 * @see ToAllMessageAction
 * @see GiveAction
 * @see OpenActionInventoryAction
 */
public class CommandAction extends BasicAction {
	/**The command to execute*/
	private String command;
	/**True - the command will be executed by the server<br>
	 * False - the command will be executed by the player*/
	private boolean fromServer;
	/**True - the player will be opped to execute the command and then deopped<br>
	 * False - the player will not be opped and the command may give an error if it requires op*/
	private boolean makeTempOp;
	
	@Override
	public void internalClick() {
		ServerPlayerEntity player = gui.getPlayer();
		if (makeTempOp && !fromServer) {
			PlayerManager manager = player.server.getPlayerManager();
			GameProfile profile = player.getGameProfile();
			if (manager.isOperator(profile)) {
				MessageHelper.executeCommand(player, command, false);
			} else {
				manager.addToOperators(profile);
				MessageHelper.executeCommand(player, command, false);
				manager.removeFromOperators(profile);
			}
		} else {
			MessageHelper.executeCommand(player, command, true);
		}
	}
}