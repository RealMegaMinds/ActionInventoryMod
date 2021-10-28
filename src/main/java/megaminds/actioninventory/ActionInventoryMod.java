package megaminds.actioninventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.util.WorldSavePath;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import megaminds.actioninventory.commands.Commands;
import megaminds.actioninventory.inventory.helpers.ActionManager;
import megaminds.actioninventory.listeners.BlockListener;
import megaminds.actioninventory.listeners.EntityListener;
import megaminds.actioninventory.listeners.ItemListener;
import megaminds.actioninventory.listeners.SignListener;

public class ActionInventoryMod implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger();

	public static final String MOD_ID = "actioninventory";
	public static final String MOD_NAME = "Action Inventory Mod";
	
	@Override
	public void onInitialize() {
		info("Initializing");
		
		ServerLifecycleEvents.SERVER_STARTED.register((server)->{
			ActionManager.onStartUp(server.getSavePath(WorldSavePath.ROOT));
		});
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, manager)->{
			ActionManager.onShutDown(server.getSavePath(WorldSavePath.ROOT));
			ActionManager.onStartUp(server.getSavePath(WorldSavePath.ROOT));
		});
		ServerLifecycleEvents.SERVER_STOPPING.register((server)->{
			ActionManager.onShutDown(server.getSavePath(WorldSavePath.ROOT));
		});
		
		CommandRegistrationCallback.EVENT.register(Commands::register);
		UseItemCallback.EVENT.register(ItemListener::onItemUse);
		UseBlockCallback.EVENT.register(SignListener::onSignBlockUse);
		UseBlockCallback.EVENT.register(BlockListener::onBlockUse);
		AttackBlockCallback.EVENT.register(BlockListener::onBlockAttack);
		UseEntityCallback.EVENT.register(EntityListener::onEntityUse);
		AttackEntityCallback.EVENT.register(EntityListener::onEntityAttack);
		
				
		info("Initialized");
	}
	
	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void log(Level level, String message){
		LOGGER.log(level, message);
	}
}