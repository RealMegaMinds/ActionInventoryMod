package megaminds.actioninventory;

@FunctionalInterface
public interface ExceptionSupplier<T> {
	T get() throws Exception;
}
