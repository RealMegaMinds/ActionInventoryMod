package megaminds.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.util.TextCollector;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.Registry;

import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import megaminds.testmod.callbacks.InventoryEvents;
import megaminds.testmod.callbacks.SignFinishCallback;
import megaminds.testmod.commands.Commands;
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

	@Override
	public void onInitialize() {
		info("Initializing");
		//NEW STUFF
		ServerWorldEvents.LOAD.register((server, world)->{
			dataFolder = server.getSavePath(WorldSavePath.ROOT).resolve(MOD_NAME);
			server.addServerGuiTickable(()->new TickingTask(server));
		});
		CommandRegistrationCallback.EVENT.register(Commands::register);
		UseItemCallback.EVENT.register(ItemListener::onItemUse);
		SignFinishCallback.EVENT.register(SignListener::onSignChange);
		UseBlockCallback.EVENT.register(SignListener::onSignUse);
		UseBlockCallback.EVENT.register(BlockListener::onBlockUse);
		AttackBlockCallback.EVENT.register(BlockListener::onBlockAttack);
		AttackBlockCallback.EVENT.register(SignListener::onSignAttack);
		InventoryEvents.BEFORE_SLOT_CLICK.register(InventoryListener::onInventoryClick);
		UseEntityCallback.EVENT.register(EntityListener::onEntityUse);
		AttackEntityCallback.EVENT.register(EntityListener::onEntityAttack);
		
		
			//OLD STUFF
//		InventoryEvents.BEFORE_SLOT_CLICK.register((packet, player)->{
//			return TypedActionResult.pass(packet);
//		});
//		InventoryEvents.AFTER_SLOT_CLICK.register((packet, player)->{
//			return ActionResult.PASS;
//		});
//						
//		InventoryClickCallback.EVENT.register((slot, button, slotActionType, player)->{
//			if (slot==null || player.world.isClient) return ActionResult.PASS;
//			info(slot.getStack().toString());
//			if (slot.inventory == player.getInventory()) {
//				
//			} else {
//				if (slot.inventory instanceof CommandInventory) {
//					
//				}
//			}
//			return ActionResult.FAIL;
//		});
//		
//		
//		UseItemCallback.EVENT.register((player, world, hand)->{
//			if (!world.isClient && hand==Hand.MAIN_HAND && player.isCreativeLevelTwoOp()) {
//				NamedScreenHandlerFactory factory = CommandInventory.get(player.getMainHandStack());
//				if (factory==null) {
//					return TypedActionResult.fail(ItemStack.EMPTY);
//				}
//				((ServerPlayerEntity)player).openHandledScreen(factory);
//			}
//			return TypedActionResult.pass(ItemStack.EMPTY);
//		});
//
//		new CommandInventory(new LiteralText("Super Power List"), new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.ACACIA_BOAT), new ItemStack(Items.BIRCH_DOOR));
		info("Initialized");
	}
	
	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void log(Level level, String message){
		LOGGER.log(level, "["+MOD_NAME+"] " + message);
	}
}