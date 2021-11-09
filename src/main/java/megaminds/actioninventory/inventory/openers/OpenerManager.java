package megaminds.actioninventory.inventory.openers;

import megaminds.actioninventory.TypeManager;

public class OpenerManager extends TypeManager<Opener> {
	public static final OpenerManager INSTANCE = new OpenerManager();
	
	private OpenerManager() {}

	static {
		INSTANCE.add(NameOpener.class);
		INSTANCE.add(TagOpener.class);
		INSTANCE.add(NbtOpener.class);
		INSTANCE.add(PosOpener.class);
		INSTANCE.add(UUIDOpener.class);
		INSTANCE.add(TeamOpener.class);
		INSTANCE.add(IdentifierOpener.class);
	}

	@Override
	public String getTypeValue(Class<? extends Opener> clazz) {
		String className = clazz.getSimpleName();
		return (className.endsWith("Opener") ? className.substring(0, className.length()-"Opener".length()) : className).toLowerCase();
	}
}