package megaminds.actioninventory.util;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;

public class Printer {
	public static void print(ScreenHandlerType<?> handler) {
		System.out.println(Registry.SCREEN_HANDLER.getId(handler).toString());
	}
	
	public static void test() {
//		print(ScreenHandlerType.GENERIC_9X6);
	}
}