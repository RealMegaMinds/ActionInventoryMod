package megaminds.testmod.actions;

import com.mojang.authlib.GameProfile;

import megaminds.testmod.Helper;
import megaminds.testmod.actions.messaging.ToPlayerMessageAction;
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
public class CommandAction implements Action {
	private final String command;
	private final boolean fromServer;
	private final boolean makeTempOp;
	
	/**
	 * @param command
	 * The command to execute
	 * @param fromServer
	 * True - the command will be executed by the server
	 * False - the command will be executed by the player
	 * @param makeTempOp
	 * True - the player will be opped to execute the command and them deopped
	 * False - the player will not be opped and the command may give an error if it requires op
	 */
	public CommandAction(String command, boolean fromServer, boolean makeTempOp) {
		this.command = command;
		this.fromServer = fromServer;
		this.makeTempOp = makeTempOp;
	}

	@Override
	public void execute(ServerPlayerEntity player) {
		if (makeTempOp) {
			PlayerManager manager = player.server.getPlayerManager();
			GameProfile profile = player.getGameProfile();
			if (manager.isOperator(profile)) {
				Helper.executeCommand(player, command, fromServer);
			} else {
				manager.addToOperators(profile);
				Helper.executeCommand(player, command, fromServer);
				manager.removeFromOperators(profile);
			}
		} else {
			Helper.executeCommand(player, command, fromServer);
		}
	}
}