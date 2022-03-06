package megaminds.actioninventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.util.Random;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import megaminds.actioninventory.commands.Commands;
import megaminds.actioninventory.loaders.BasicOpenerLoader;
import megaminds.actioninventory.loaders.ActionInventoryLoader;
import megaminds.actioninventory.misc.Saver;
import megaminds.actioninventory.openers.BlockOpener;
import megaminds.actioninventory.openers.EntityOpener;
import megaminds.actioninventory.openers.ItemOpener;

public class ActionInventoryMod implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final Random RANDOM = new Random();
	public static final String MOD_ID = "actioninventory";
	public static final String MOD_NAME = "Action Inventory Mod";

	private static final ActionInventoryConfig CONFIG = new ActionInventoryConfig();

	@Override
	public void onInitialize() {
		CONFIG.load();

		ServerLifecycleEvents.SERVER_STARTED.register(server->{
			Saver.setSavesDir(ActionInventoryConfig.getSavePath(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)));
			ActionInventoryConfig.getGuiBuilderPaths(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)).forEach(ActionInventoryLoader::load);
			ActionInventoryConfig.getOpenerPaths(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)).forEach(BasicOpenerLoader::load);
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server->{
			ActionInventoryLoader.clear();
			BasicOpenerLoader.clear();
			Saver.saveAll();
			Saver.clear();
		});

		ItemOpener.registerCallbacks();
		BlockOpener.registerCallbacks();
		EntityOpener.registerCallbacks();
		CommandRegistrationCallback.EVENT.register(Commands::register);

		info("Initialized");
	}

	public static void reload(MinecraftServer server) {
		ActionInventoryLoader.clear();
		BasicOpenerLoader.clear();
		Saver.saveAll();
		Saver.clear();
		Saver.setSavesDir(ActionInventoryConfig.getSavePath(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)));
		ActionInventoryConfig.getGuiBuilderPaths(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)).forEach(ActionInventoryLoader::load);
		ActionInventoryConfig.getOpenerPaths(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)).forEach(BasicOpenerLoader::load);
	}

	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void warn(String message) {
		LOGGER.warn(message);
	}

	public static void log(Level level, String message){
		LOGGER.log(level, message);
	}
}