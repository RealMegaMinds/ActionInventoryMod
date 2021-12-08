package megaminds.actioninventory.inventory.openers;

public interface Opener {
	public static enum What {Block, BlockEntity, ItemStack, Entity}
	public static enum ClickType {ATTACK, USE}

	/**
	 * Checks if opening is allowed.
	 * Should not throw errors for any reason, but instead return false.
	 */
	public abstract boolean canOpen(Object o, ClickType click, What what);
	
	/**
	 * This is called when an inventory is actually opened. This is only called for the opener that returned true for {@link #canOpen(Object, ClickType, What)}
	 * and is called with the same arguments.
	 */
	//public abstract void onOpen(Object o, ClickType click, What what);
}