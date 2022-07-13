package megaminds.actioninventory.serialization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import megaminds.actioninventory.util.annotations.Exclude;

public class ExcludeStrategy implements ExclusionStrategy {
	private final boolean serialize;

	/**
	 * True -> for serialization
	 * False -> for deserialization
	 */
	public ExcludeStrategy(boolean serialize) {
		this.serialize = serialize;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes field) {
		var anno = field.getAnnotation(Exclude.class);
		return anno != null && (serialize ? anno.serialize() : anno.deserialize());
	}
}