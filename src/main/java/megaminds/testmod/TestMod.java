package megaminds.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.WorldSavePath;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import megaminds.testmod.callbacks.InventoryEvents;
import megaminds.testmod.callbacks.SignFinishCallback;
import megaminds.testmod.commands.Commands;
import megaminds.testmod.inventory.viewable.ViewableActionInventory;
import megaminds.testmod.inventory.viewable.ViewableManager;
import megaminds.testmod.listeners.BlockListener;
import megaminds.testmod.listeners.EntityListener;
import megaminds.testmod.listeners.InventoryListener;
import megaminds.testmod.listeners.ItemListener;
import megaminds.testmod.listeners.SignListener;

public class TestMod implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger();

	public static final String MOD_ID = "testmod";
	public static final String MOD_NAME = "Test Mod";
	
	public static Path dataFolder;

	static class Fact implements NamedScreenHandlerFactory{
		Inventory inv;
		public Fact(Inventory inv) {
			this.inv = inv;
		}
		@Override
		public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
			return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, inv);
		}

		@Override
		public Text getDisplayName() {
			return new LiteralText(MOD_NAME);
		}
	}
	@Override
	public void onInitialize() {
		info("Initializing");
				
		//NEWER STUFF
//		ServerWorldEvents.LOAD.register((server, world)->{
//			ViewableManager.load(server.getSavePath(WorldSavePath.ROOT));
//		});
		
		
		//NEW STUFF
//		ServerWorldEvents.LOAD.register((server, world)->{
//			dataFolder = server.getSavePath(WorldSavePath.ROOT).resolve(MOD_NAME);
//		});
//		CommandRegistrationCallback.EVENT.register(Commands::register);
//		UseItemCallback.EVENT.register(ItemListener::onItemUse);
//		SignFinishCallback.EVENT.register(SignListener::onSignChange);
//		UseBlockCallback.EVENT.register(SignListener::onSignUse);
//		UseBlockCallback.EVENT.register(BlockListener::onBlockUse);
//		AttackBlockCallback.EVENT.register(BlockListener::onBlockAttack);
//		AttackBlockCallback.EVENT.register(SignListener::onSignAttack);
//		InventoryEvents.BEFORE_SLOT_CLICK.register(InventoryListener::onInventoryClick);
//		UseEntityCallback.EVENT.register(EntityListener::onEntityUse);
//		AttackEntityCallback.EVENT.register(EntityListener::onEntityAttack);
				
		info("Initialized");
	}
	
	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void log(Level level, String message){
		LOGGER.log(level, "["+MOD_NAME+"] " + message);
	}
}