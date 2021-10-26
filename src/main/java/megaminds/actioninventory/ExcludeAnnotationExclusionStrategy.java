package megaminds.actioninventory;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ExcludeAnnotationExclusionStrategy {
	public static final ExclusionStrategy SerializeStrategy = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			final Exclude a = f.getAnnotation(Exclude.class);
			return a!=null && a.serialize();
		}
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {return false;}
	};
	public static final ExclusionStrategy DeserializeStrategy = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			final Exclude a = f.getAnnotation(Exclude.class);
			return a!=null && a.deserialize();
		}
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {return false;}
	};
}