package megaminds.actioninventory.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class SpecifiedExcluder implements ExclusionStrategy {
	private static final Map<Class<?>, List<String>> EXCLUDES = new HashMap<>();
	
	public static void addExcludes(Class<?> clazz, String... fieldNames) {
		if (clazz==null||fieldNames==null) return;
		EXCLUDES.computeIfAbsent(clazz, c->new ArrayList<>()).addAll(Arrays.asList(fieldNames));
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		List<String> list = EXCLUDES.get(f.getDeclaringClass());
		return list!=null&&list.contains(f.getName());
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}	
}