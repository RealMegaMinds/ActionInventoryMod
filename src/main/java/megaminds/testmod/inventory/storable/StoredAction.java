package megaminds.testmod.inventory.storable;

public class StoredAction {
	public enum Type {COMMAND, DISABLED, GIVE, OPEN, SOUND, MESSAGE}
	
	public final Type type;
	public final Object argument;
	
	public StoredAction() {
		this.type = null;
		this.argument = null;
	}
	
	public StoredAction(Type type, Object argument) {
		this.type = type;
		this.argument = argument;
	}
}