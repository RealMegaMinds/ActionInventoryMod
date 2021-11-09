package megaminds.actioninventory.inventory.openers;

public abstract class BaseOpener implements Opener {
	private ClickType clickType;
	private What what;
	
	public ClickType getClickType() {
		return clickType;
	}
	
	public What getWhat() {
		return what;
	}
	
	public boolean isValidClick(ClickType click) {
		return click==clickType;
	}
	
	public boolean isValidWhat(What what) {
		return this.what==what;
	}
}