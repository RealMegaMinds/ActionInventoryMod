package megaminds.actioninventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import megaminds.actioninventory.api.base.ActionObjectHandler;

public class ActionInventoryMod implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "actioninventory";
	public static final String MOD_NAME = "Action Inventory Mod";

	private static MinecraftServer currentServer;
	private static Path GLOBAL_DIR;
	private static Path SERVER_DIR;
	
	@Override
	public void onInitialize() {
		info("Initializing");
		
		GLOBAL_DIR = FabricLoader.getInstance().getGameDir().resolve(MOD_ID);
		
		ServerLifecycleEvents.SERVER_STARTED.register((server)->{
			currentServer = server;
			SERVER_DIR = server.getSavePath(WorldSavePath.ROOT).resolve(MOD_ID);
			if (checkDir()) {
				ActionObjectHandler.load();
			}
		});
		ServerLifecycleEvents.SERVER_STOPPED.register((server)->{
			ActionObjectHandler.stop(checkDir());
			currentServer = null;
			ROOT_DIR = null;
		});
		
//		CommandRegistrationCallback.EVENT.register(Commands::register);
//		UseItemCallback.EVENT.register(ItemListener::onItemUse);
//		UseBlockCallback.EVENT.register(SignListener::onSignBlockUse);
//		UseBlockCallback.EVENT.register(BlockListener::onBlockUse);
//		AttackBlockCallback.EVENT.register(BlockListener::onBlockAttack);
//		UseEntityCallback.EVENT.register(EntityListener::onEntityUse);
//		AttackEntityCallback.EVENT.register(EntityListener::onEntityAttack);
		info("Initialized");
	}
	
	public static MinecraftServer getCurrentServer() {
		return currentServer;
	}
	
	public static Path createDir(Path p) {
		if (Files.notExists(p)) {
			try {
				Files.createDirectory(p);
			} catch (IOException e) {
				log(Level.WARN, "Couldn't create directory: "+p);
				e.printStackTrace();
				return null;
			}
		}
		return p;
	}
	
	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void log(Level level, String message){
		LOGGER.log(level, message);
	}
}