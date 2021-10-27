package megaminds.actioninventory.inventory.openers;

import megaminds.actioninventory.inventory.helpers.Typed;
import megaminds.actioninventory.inventory.openers.Opener.Type;

public abstract class Opener extends Typed<Type> {
	public enum Type {
		NAME(NameOpener.class), TAG(TagOpener.class), NBT(NbtOpener.class), POS(PosOpener.class), TYPE(TypeOpener.class), UUID(UUIDOpener.class), TEAM(TeamOpener.class);
		public final Class<? extends Opener> clazz;
		private Type(Class<? extends Opener> clazz) {
			this.clazz = clazz;
		}
	}
	public enum What {Block, BlockEntity, ItemStack, Entity}
	public enum ClickType {ATTACK, USE}
	
	private ClickType clickType;
	private What what;
	
	public ClickType getClickType() {
		return clickType;
	}
	
	public boolean isValidClick(ClickType click) {
		return click==clickType;
	}
	
	public What getWhat() {
		return what;
	}
	
	public boolean isValidWhat(What what) {
		return this.what==what;
	}
	
	/**
	 * Checks if opening is allowed.
	 * Should return false if the passed in object doesn't match {@link Opener#what}
	 */
	public abstract boolean canOpen(Object o, ClickType click, What what);
	
	@Override
	public String toString() {
		return "Opener"+super.toString()+", clickType="+clickType+", what="+what;
	}
}