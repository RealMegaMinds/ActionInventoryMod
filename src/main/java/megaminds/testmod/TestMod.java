package megaminds.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
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
import net.minecraft.util.registry.Registry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.filoghost.chestcommands.listener.CommandListener;
import megaminds.testmod.callbacks.InventoryEvents;

public class TestMod implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger();

	public static final String MOD_ID = "testmod";
	public static final String MOD_NAME = "Test Mod";

	@Override
	public void onInitialize() {
		info("Initializing");

		InventoryEvents.BEFORE_SLOT_CLICK.register((packet, player)->{
			info("bsc");
			return TypedActionResult.pass(packet);
		});
		InventoryEvents.AFTER_SLOT_CLICK.register((packet, player)->{
			info("asc");
			return ActionResult.PASS;
		});
						
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
	}
	
	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void log(Level level, String message){
		LOGGER.log(level, "["+MOD_NAME+"] " + message);
	}
}