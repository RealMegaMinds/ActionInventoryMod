package megaminds.actioninventory.util;

import net.minecraft.screen.ScreenHandlerType;

public class GuiHelper {
	public static ScreenHandlerType<?> getHandler(int rows) {
		if (rows<1) throw new IllegalArgumentException("Rows cannot be less than 1.");
		if (rows>6) throw new IllegalArgumentException("Rows cannot be greater than 6.");
		
		return switch (rows) {
		case 1 -> ScreenHandlerType.GENERIC_9X1;
		case 2 -> ScreenHandlerType.GENERIC_9X2;
		case 3 -> ScreenHandlerType.GENERIC_9X3;
		case 4 -> ScreenHandlerType.GENERIC_9X4;
		case 5 -> ScreenHandlerType.GENERIC_9X5;
		case 6 -> ScreenHandlerType.GENERIC_9X6;
		default -> throw new IllegalArgumentException("Unexpected value: " + rows);
		};
	}
}