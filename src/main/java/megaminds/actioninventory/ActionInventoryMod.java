package megaminds.actioninventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.WorldSavePath;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import megaminds.actioninventory.util.NamedGuiLoader;
import megaminds.actioninventory.util.Saver;
import megaminds.actioninventory.openers.BlockOpener;
import megaminds.actioninventory.openers.EntityOpener;
import megaminds.actioninventory.openers.ItemOpener;
import megaminds.actioninventory.util.BasicOpenerLoader;

public class ActionInventoryMod implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "actioninventory";
	public static final String MOD_NAME = "Action Inventory Mod";

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register((server)->{
			Saver.setSavesDir(ActionInventoryConfig.getSavePath(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)));
			ActionInventoryConfig.getGuiBuilderPaths(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)).forEach(NamedGuiLoader::load);
			ActionInventoryConfig.getOpenerPaths(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)).forEach(BasicOpenerLoader::load);
		});
		
		ServerLifecycleEvents.SERVER_STOPPED.register((server)->{
			NamedGuiLoader.clear();
			BasicOpenerLoader.clear();
			Saver.saveAll();
			Saver.clear();
		});
		
		UseItemCallback.EVENT.register((p,w,h)->{
			if (w.isClient) return TypedActionResult.pass(ItemStack.EMPTY);
			return ItemOpener.tryOpen((ServerPlayerEntity)p, p.getStackInHand(h)) ? TypedActionResult.success(ItemStack.EMPTY) : TypedActionResult.pass(ItemStack.EMPTY);
		});
		UseBlockCallback.EVENT.register((p,w,h,r)->{
			if (w.isClient) return ActionResult.PASS;
			return BlockOpener.tryOpen((ServerPlayerEntity)p, w.getBlockState(r.getBlockPos()).getBlock(), r.getBlockPos(), w.getBlockEntity(r.getBlockPos())) ? ActionResult.SUCCESS : ActionResult.PASS;
		});
		AttackBlockCallback.EVENT.register((p,w,h,b,d)->{
			if (w.isClient) return ActionResult.PASS;
			return BlockOpener.tryOpen((ServerPlayerEntity)p, w.getBlockState(b).getBlock(), b, w.getBlockEntity(b)) ? ActionResult.SUCCESS : ActionResult.PASS;
		});
		UseEntityCallback.EVENT.register((p,w,h,e,r)->{
			return EntityOpener.tryOpen((ServerPlayerEntity)p, e) ? ActionResult.SUCCESS : ActionResult.PASS;
		});
		AttackEntityCallback.EVENT.register((p,w,h,e,r)->{
			return EntityOpener.tryOpen((ServerPlayerEntity)p, e) ? ActionResult.SUCCESS : ActionResult.PASS;
		});
		
		info("Initialized");
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