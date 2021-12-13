package megaminds.actioninventory.util;

@FunctionalInterface
public interface ExceptionSupplier<T> {
	T get() throws Exception;
}
