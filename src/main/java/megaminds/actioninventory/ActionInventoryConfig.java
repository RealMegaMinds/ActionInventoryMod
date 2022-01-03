package megaminds.actioninventory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import megaminds.actioninventory.util.Helper;

public class ActionInventoryConfig {
	//Paths can start with GLOBAL to start in the game directory
	//				or with WORLD to start in the save folder of the current world
	public static List<String> guiBuilderPaths = Collections.singletonList("GLOBAL/"+ActionInventoryMod.MOD_ID+"/guiBuilders");
	public static List<String> openerPaths = Collections.singletonList("GLOBAL/"+ActionInventoryMod.MOD_ID+"/openers");
	public static String savePath = "GLOBAL/"+ActionInventoryMod.MOD_ID+"/saves";
	
	public static List<Path> getGuiBuilderPaths(Path global, Path server) {
		return Helper.resolvePaths(guiBuilderPaths, global, server);
	}
	
	public static List<Path> getOpenerPaths(Path global, Path server) {
		return Helper.resolvePaths(openerPaths, global, server);
	}
	
	public static Path getSavePath(Path global, Path server) {
		return Helper.resolvePath(savePath, global, server);
	}
}