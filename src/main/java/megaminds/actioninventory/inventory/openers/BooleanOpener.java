package megaminds.actioninventory.inventory.openers;

public class BooleanOpener extends Opener {
	private boolean isAllowed;

	@Override
	public boolean canOpen(Object o, ClickType click, What what) {
		return isValidWhat(what)&&isAllowed&& (what==What.Sign?isValidClick(click):true);
	}

	@Override
	protected Type getTypeInternal() {
		return Type.BOOLEAN;
	}
	
	@Override
	public String toString() {
		return super.toString()+", isAllowed="+isAllowed+"]";
	}
}