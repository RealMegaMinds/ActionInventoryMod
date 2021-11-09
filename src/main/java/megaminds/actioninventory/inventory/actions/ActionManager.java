package megaminds.actioninventory.inventory.actions;

import megaminds.actioninventory.TypeManager;
import megaminds.actioninventory.inventory.actions.messaging.ToAllMessageAction;
import megaminds.actioninventory.inventory.actions.messaging.ToMultiMessageAction;
import megaminds.actioninventory.inventory.actions.messaging.ToPlayerMessageAction;
import megaminds.actioninventory.inventory.actions.messaging.ToServerMessageAction;
import megaminds.actioninventory.inventory.actions.messaging.ToSingleMessageAction;

public class ActionManager extends TypeManager<Action> {
	public static final ActionManager INSTANCE = new ActionManager();
	
	static {
		INSTANCE.add(CommandAction.class);
		INSTANCE.add(GiveAction.class);
		INSTANCE.add(OpenActionInventoryAction.class);
		INSTANCE.add(SoundAction.class);
		INSTANCE.add(ToAllMessageAction.class);
		INSTANCE.add(ToMultiMessageAction.class);
		INSTANCE.add(ToPlayerMessageAction.class);
		INSTANCE.add(ToServerMessageAction.class);
		INSTANCE.add(ToSingleMessageAction.class);
	}
	
	private ActionManager() {}
	
	@Override
	public String getTypeValue(Class<? extends Action> clazz) {
		String className = clazz.getSimpleName();
		return (className.endsWith("Action") ? className.substring(0, className.length()-"Action".length()) : className).toLowerCase();
	}
}