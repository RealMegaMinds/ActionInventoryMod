package megaminds.actioninventory.inventory.openers;

public class BooleanOpener extends Opener {
	private boolean isAllowed;

	@Override
	public boolean canOpen(Object o, ClickType click, What when) {
		return isValidWhat(when)&&isAllowed;
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