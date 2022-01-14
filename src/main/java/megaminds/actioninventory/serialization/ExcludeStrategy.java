package megaminds.actioninventory.serialization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import megaminds.actioninventory.util.annotations.Exclude;

public class ExcludeStrategy implements ExclusionStrategy {
	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes field) {
		return field.getAnnotation(Exclude.class) != null;
	}
}