package megaminds.testmod.inventory.storable;

public class StoredOpenChecker {
	public static enum OpenType {BLOCK, BLOCK_ENTITY, ITEM, COMMAND, INV_CLICK, SIGN}
	public static enum ClickType {ATTACK, USE}
	public static enum ArgType {CUSTOM_NAME, REAL_NAME, TAG, NBT, POS, TYPE, UUID, TEAM}

	public final OpenType openType;
	public final ClickType clickType;
	public final ArgType argType;
	public final Object arg;
	
	public StoredOpenChecker() {
		this.openType = null;
		this.clickType = null;
		this.argType = null;
		this.arg = null;
	}

	public StoredOpenChecker(OpenType openType, ClickType clickType, ArgType argType, Object arg) {
		this.openType = openType;
		this.clickType = clickType;
		this.argType = argType;
		this.arg = arg;
	}
}