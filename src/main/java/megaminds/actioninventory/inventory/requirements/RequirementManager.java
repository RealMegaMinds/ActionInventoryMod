package megaminds.actioninventory.inventory.requirements;

import megaminds.actioninventory.TypeManager;

public class RequirementManager extends TypeManager<Requirement> {
	public static final RequirementManager INSTANCE = new RequirementManager();
	
	static {
		INSTANCE.add(XPRequirement.class);
	}
	
	private RequirementManager() {}

	@Override
	public String getTypeValue(Class<? extends Requirement> clazz) {
		String className = clazz.getSimpleName();
		return (className.endsWith("Requirement") ? className.substring(0, className.length()-"Requirement".length()) : className).toLowerCase();
	}
}