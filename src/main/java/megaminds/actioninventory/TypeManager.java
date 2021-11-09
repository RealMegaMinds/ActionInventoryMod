package megaminds.actioninventory;

import java.util.ArrayList;
import java.util.List;

public abstract class TypeManager<T> {
	private final List<Class<? extends T>> types = new ArrayList<>();

	public Class<? extends T> get(String name) {
		for (Class<? extends T> clazz : types) {
			if (getTypeValue(clazz).equals(name)) {
				return clazz;
			}
		}
		return null;
	}
		
	public boolean add(Class<? extends T> openerClass) {
		if (openerClass!=null) {
			return types.add(openerClass);
		} else {
			return false;
		}
	}
	
	public String getTypeValue(Class<? extends T> clazz) {
		return clazz.getSimpleName();
	}
}