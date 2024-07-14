package megaminds.actioninventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import megaminds.actioninventory.commands.Commands;
import megaminds.actioninventory.loaders.BasicOpenerLoader;
import megaminds.actioninventory.loaders.ActionInventoryLoader;
import megaminds.actioninventory.openers.BlockOpener;
import megaminds.actioninventory.openers.EntityOpener;
import megaminds.actioninventory.openers.ItemOpener;
import megaminds.actioninventory.serialization.Serializer;

//TODO Create seperate guis for input related actions
//TODO Look into CODECS
//TODO Add ability to check against luckperms permissions with requirement action
//TOOD Add ability to skip openrequirement with OpenAction
public class ActionInventoryMod implements ModInitializer {
	public static final Random RANDOM = new Random();
	public static final String MOD_ID = "actioninventory";
	public static final String MOD_NAME = "Action Inventory Mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ActionInventoryLoader INVENTORY_LOADER = new ActionInventoryLoader();
	public static final BasicOpenerLoader OPENER_LOADER = new BasicOpenerLoader();

	private static Gson gson;
	private static MinecraftServer server;

	@Override
	@SuppressWarnings("java:S2696")
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(s -> server = s);
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> server = null);
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> gson = Serializer.constructGson(server.getRegistryManager()));
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> gson = null);

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INVENTORY_LOADER);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(OPENER_LOADER);
		ItemOpener.registerCallbacks();
		BlockOpener.registerCallbacks();
		EntityOpener.registerCallbacks();
		CommandRegistrationCallback.EVENT.register(Commands::register);

		info("Initialized");
	}
	
	public static MinecraftServer getServer() throws IllegalStateException {
		if (server == null) {
			throw new IllegalStateException();
		}
		return server;
	}

	public static Gson getGson() {
		return gson;
	}

	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void warn(String message) {
		LOGGER.warn(message);
	}
}