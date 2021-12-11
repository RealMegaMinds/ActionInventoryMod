package megaminds.actioninventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import megaminds.actioninventory.api.gui.ActionInventoryManager;

public class ActionInventoryMod implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "actioninventory";
	public static final String MOD_NAME = "Action Inventory Mod";

	private static MinecraftServer currentServer;
	private static Path ROOT_DIR;
	
	@Override
	public void onInitialize() {
		info("Initializing");
		
		ServerLifecycleEvents.SERVER_STARTED.register((server)->{
			currentServer = server;
			ROOT_DIR = server.getSavePath(WorldSavePath.ROOT).resolve(MOD_ID);
			if (checkDir()) {
				ActionInventoryManager.load();
			}
		});
		ServerLifecycleEvents.SERVER_STOPPED.register((server)->{
			ActionInventoryManager.stop(checkDir());
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
	
	public static Path getModRootDir() {
		return ROOT_DIR;
	}
	
	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void log(Level level, String message){
		LOGGER.log(level, message);
	}
	
	private boolean checkDir() {
		if (ROOT_DIR==null) return false;
		
		if (Files.notExists(ROOT_DIR)) {
			try {
				Files.createDirectory(ROOT_DIR);
			} catch (IOException e) {
				log(Level.WARN, "Couldn't create mod directory: ");
				e.printStackTrace();
				ROOT_DIR = null;
				return false;
			}
		}
		if (!Files.isDirectory(ROOT_DIR)) {
			log(Level.WARN, "Path: "+ROOT_DIR.toString()+" needs to be a directory.");
			return false;
		}
		return true;
	}
}