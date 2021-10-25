package megaminds.actioninventory.inventory;

public abstract class Typed<T extends Enum<T>> {
	private final T type = getTypeInternal();

	public T getType() {
		return type;
	}
	protected abstract T getTypeInternal();
	
	@Override
	public String toString() {
		return "[type="+type;
	}
}