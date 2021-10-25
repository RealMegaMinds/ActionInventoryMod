package megaminds.testmod.inventory.actions;

import megaminds.testmod.inventory.Typed;
import megaminds.testmod.inventory.actions.Action.Type;
import megaminds.testmod.inventory.actions.messaging.ToAllMessageAction;
import megaminds.testmod.inventory.actions.messaging.ToMultiMessageAction;
import megaminds.testmod.inventory.actions.messaging.ToPlayerMessageAction;
import megaminds.testmod.inventory.actions.messaging.ToServerMessageAction;
import megaminds.testmod.inventory.actions.messaging.ToSingleMessageAction;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Actions represent things done when an item is clicked in an ActionInventory
 */
public abstract class Action extends Typed<Type> {
	public static enum Type {
		Command(CommandAction.class), Give(GiveAction.class), Open(OpenActionInventoryAction.class), Sound(SoundAction.class),
		AllMessage(ToAllMessageAction.class), MultiMessage(ToMultiMessageAction.class), PlayerMessage(ToPlayerMessageAction.class),
		ServerMessage(ToServerMessageAction.class), SingleMessage(ToSingleMessageAction.class);
		
		public final Class<? extends Action> clazz;
		private Type(Class<? extends Action> clazz) {
			this.clazz = clazz;
		}
	}
	public abstract void execute(ServerPlayerEntity player);
}