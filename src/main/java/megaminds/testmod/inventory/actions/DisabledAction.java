package megaminds.testmod.inventory.actions;

import megaminds.testmod.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * This represents a disabled action that sends an error message when clicked
 */
public class DisabledAction implements Action {
	private final Text errorMessage;

	/**
	 * @param errorMessage
	 * The error message to send
	 */
	public DisabledAction(Text errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public void execute(ServerPlayerEntity player) {
		MessageHelper.toPlayerMessage(player, errorMessage, true);
	}
}