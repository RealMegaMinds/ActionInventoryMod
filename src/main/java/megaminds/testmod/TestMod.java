package megaminds.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.util.WorldSavePath;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import megaminds.testmod.callbacks.SignFinishCallback;
import megaminds.testmod.commands.Commands;
import megaminds.testmod.inventory.ActionManager;
import megaminds.testmod.listeners.BlockListener;
import megaminds.testmod.listeners.EntityListener;
import megaminds.testmod.listeners.ItemListener;
import megaminds.testmod.listeners.SignListener;

public class TestMod implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger();

	public static final String MOD_ID = "testmod";
	public static final String MOD_NAME = "Test Mod";
	
	@Override
	public void onInitialize() {
		info("Initializing");
		ServerWorldEvents.LOAD.register((server, world)->{
			ActionManager.onStartUp(server.getSavePath(WorldSavePath.ROOT));
		});
		ServerWorldEvents.UNLOAD.register((server, world)->{
			ActionManager.onShutDown(server.getSavePath(WorldSavePath.ROOT));
		});
		
		
		CommandRegistrationCallback.EVENT.register(Commands::register);
		UseItemCallback.EVENT.register(ItemListener::onItemUse);
		SignFinishCallback.EVENT.register(SignListener::onSignChange);
		UseBlockCallback.EVENT.register(SignListener::onSignUse);
		UseBlockCallback.EVENT.register(BlockListener::onBlockUse);
		AttackBlockCallback.EVENT.register(BlockListener::onBlockAttack);
		AttackBlockCallback.EVENT.register(SignListener::onSignAttack);
		UseEntityCallback.EVENT.register(EntityListener::onEntityUse);
		AttackEntityCallback.EVENT.register(EntityListener::onEntityAttack);
				
		info("Initialized");
	}
	
	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void log(Level level, String message){
		LOGGER.log(level, "["+MOD_NAME+"] " + message);
	}
}