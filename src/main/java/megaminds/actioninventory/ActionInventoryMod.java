package megaminds.actioninventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.layered.Layer;
import eu.pb4.sgui.api.gui.layered.LayeredGui;
import megaminds.actioninventory.commands.Commands;
import megaminds.actioninventory.gui.BetterAnvilGui;
import megaminds.actioninventory.gui.BetterGui;
import megaminds.actioninventory.gui.ListedGui;
import megaminds.actioninventory.gui.PagedGui;
import megaminds.actioninventory.gui.input.BooleanInput;
import megaminds.actioninventory.gui.input.ChoiceInput;
import megaminds.actioninventory.gui.input.ColorInput;
import megaminds.actioninventory.gui.input.IdentifierInput;
import megaminds.actioninventory.gui.input.InputSplitter;
import megaminds.actioninventory.gui.input.IntInput;
import megaminds.actioninventory.gui.input.ListInput;
import megaminds.actioninventory.gui.input.NumberInputCallback;
import megaminds.actioninventory.gui.input.StringInput;
import megaminds.actioninventory.gui.input.TextInput;
import megaminds.actioninventory.loaders.BasicOpenerLoader;
import megaminds.actioninventory.loaders.NamedGuiLoader;
import megaminds.actioninventory.misc.Saver;
import megaminds.actioninventory.openers.BlockOpener;
import megaminds.actioninventory.openers.EntityOpener;
import megaminds.actioninventory.openers.ItemOpener;
import megaminds.actioninventory.util.ElementHelper;

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
			ActionInventoryConfig.getGuiBuilderPaths(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)).forEach(NamedGuiLoader::load);
			ActionInventoryConfig.getOpenerPaths(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)).forEach(BasicOpenerLoader::load);
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server->{
			NamedGuiLoader.clear();
			BasicOpenerLoader.clear();
			Saver.saveAll();
			Saver.clear();
		});

		ItemOpener.registerCallbacks();
		BlockOpener.registerCallbacks();
		EntityOpener.registerCallbacks();
		CommandRegistrationCallback.EVENT.register(Commands::register);

		UseItemCallback.EVENT.register(ActionInventoryMod::test);
		
		info("Initialized");
	}

	public static void reload(MinecraftServer server) {
		NamedGuiLoader.clear();
		BasicOpenerLoader.clear();
		Saver.saveAll();
		Saver.clear();
		Saver.setSavesDir(ActionInventoryConfig.getSavePath(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)));
		ActionInventoryConfig.getGuiBuilderPaths(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)).forEach(NamedGuiLoader::load);
		ActionInventoryConfig.getOpenerPaths(FabricLoader.getInstance().getGameDir(), server.getSavePath(WorldSavePath.ROOT)).forEach(BasicOpenerLoader::load);
	}
	
	private static TypedActionResult<ItemStack> test(PlayerEntity player, World w, Hand h) {
		if (!w.isClient) {
			ServerPlayerEntity p = (ServerPlayerEntity) player;
			Set<Item> items = Set.copyOf(Registry.ITEM.stream().toList());
			var sin = new StringInput()
					.question(new LiteralText("Enter Test"));
			
//			var g = new BetterGui(ScreenHandlerType.BLAST_FURNACE, p, false);
//			g.setSlot(0, new GuiElement(Items.ACACIA_BOAT.getDefaultStack(), new ListInput<String>(sin)
//					.toStackFunc(s->ElementHelper.stackOf(Items.PAPER, s))
//					.question(new LiteralText("This is a test."))
//					.onFinish(System.out::println)));
			
			var g = new ListedGui(4, p, false);
			var c = 0;
			for (var item : items) {
				if (c%2!=0) {
					g.addSlot(item.getDefaultStack(), (i,t,a,g2)->((ListedGui)g2).removeSlot(i));
				} else {
					g.addSlot((GuiElementInterface)null);
				}
				c++;
			}
			g.open();
		}
		return TypedActionResult.pass(ItemStack.EMPTY);
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