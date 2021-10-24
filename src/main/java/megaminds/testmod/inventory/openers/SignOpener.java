package megaminds.testmod.inventory.openers;

public class SignOpener extends BooleanOpener {
	@Override
	protected Type getTypeInternal() {
		return Type.SIGN;
	}
	
	@Override
	public boolean canOpen(Object obj, ClickType click, What what) {
		return isValidClick(click) && super.canOpen(obj, click, what);
	}
}