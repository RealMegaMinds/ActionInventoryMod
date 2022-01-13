package megaminds.actioninventory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.Config;
import megaminds.actioninventory.util.Helper;

public class ActionInventoryConfig extends Config implements ConfigGroup {
	public static final String GLOBAL = "GLOBAL/";	//starts in game directory
	public static final String WORLD = "WORLD/";	//starts in save folder of current world
	
	@ConfigEntry(comment = "The paths where ActionInventories should be read from.\nPaths starting with \"GLOBAL/\" will start in the game directory.\nPaths starting with \"WORLD/\" will start in the game folder of the world.")
	private static List<String> guiBuilderPaths = Collections.singletonList(GLOBAL+ActionInventoryMod.MOD_ID+"/guiBuilders");
	@ConfigEntry(comment = "The paths where Openers should be read from.\nPaths starting with \"GLOBAL/\" will start in the game directory.\nPaths starting with \"WORLD/\" will start in the game folder of the world.")
	private static List<String> openerPaths = Collections.singletonList(GLOBAL+ActionInventoryMod.MOD_ID+"/openers");
	@ConfigEntry(comment = "The path where consumable action save files should be kept from.\nPaths starting with \"WORLD/\" will start in the game directory.\nPaths starting with \"WORLD/\" will start in the game folder of the world.")
	private static String savePath = GLOBAL+ActionInventoryMod.MOD_ID+"/saves";
	
	public ActionInventoryConfig() {
		super(ActionInventoryMod.MOD_ID);
	}
	
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