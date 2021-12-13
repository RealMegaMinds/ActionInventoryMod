package megaminds.actioninventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.pb4.sgui.api.elements.AnimatedGuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import megaminds.actioninventory.util.ActionObjectHandler;

public class ActionInventoryMod implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "actioninventory";
	public static final String MOD_NAME = "Action Inventory Mod";

	private static MinecraftServer currentServer;
	private static ActionObjectHandler GLOBAL_HANDLER;
	@Environment(EnvType.CLIENT)
	private static ActionObjectHandler SERVER_HANDLER;
	
	@Override
	public void onInitialize() {
		info("Initializing");
		
		
		CommandRegistrationCallback.EVENT.register((d,s)->{
			d.register(CommandManager.literal("test").executes(ActionInventoryMod::test));
		});
		
		
		
//		GLOBAL_HANDLER = ActionObjectHandler.createActionObjectHandler(createDir(FabricLoader.getInstance().getGameDir().resolve(MOD_ID)), "GLOBAL");
//		GLOBAL_HANDLER.load();
//		
//		ServerLifecycleEvents.SERVER_STARTED.register((server)->{
//			currentServer = server;
//			if (FabricLoader.getInstance().getEnvironmentType()==EnvType.CLIENT) {
//				SERVER_HANDLER = ActionObjectHandler.createActionObjectHandler(createDir(server.getSavePath(WorldSavePath.ROOT).resolve(MOD_ID)), "SERVER");
//				SERVER_HANDLER.load();
//			}
//		});
//		ServerLifecycleEvents.SERVER_STOPPED.register((server)->{
//			currentServer = null;
//			if (FabricLoader.getInstance().getEnvironmentType()==EnvType.CLIENT) {
//				SERVER_HANDLER.stop();
//			}
//			GLOBAL_HANDLER.save();
//		});

//		CommandRegistrationCallback.EVENT.register(Commands::register);
//		UseItemCallback.EVENT.register(ItemListener::onItemUse);
//		UseBlockCallback.EVENT.register(SignListener::onSignBlockUse);
//		UseBlockCallback.EVENT.register(BlockListener::onBlockUse);
//		AttackBlockCallback.EVENT.register(BlockListener::onBlockAttack);
//		UseEntityCallback.EVENT.register(EntityListener::onEntityUse);
//		AttackEntityCallback.EVENT.register(EntityListener::onEntityAttack);
		info("Initialized");
	}
	
    private static int test(CommandContext<ServerCommandSource> cxt) throws CommandSyntaxException {
    	ServerPlayerEntity player = cxt.getSource().getPlayer();
    	SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X6, player, false);
    	gui.setTitle(new LiteralText("WELCOME").formatted(Formatting.GREEN));
    	gui.setSlot(7, new ItemStack(Items.DIAMOND_BLOCK, 1));
    	gui.setSlot(1, new AnimatedGuiElement(new ItemStack[]{
                Items.NETHERITE_PICKAXE.getDefaultStack(),
                Items.DIAMOND_PICKAXE.getDefaultStack(),
                Items.GOLDEN_PICKAXE.getDefaultStack(),
                Items.IRON_PICKAXE.getDefaultStack(),
                Items.STONE_PICKAXE.getDefaultStack(),
                Items.WOODEN_PICKAXE.getDefaultStack()
        }, 10, false, (x, y, z) -> {
        }));
    	gui.setSlot(20, new GuiElementBuilder()
                .setItem(Items.BARRIER)
                .glow()
                .setName(new LiteralText("Bye")
                        .setStyle(Style.EMPTY.withItalic(false).withBold(true)))
                .addLoreLine(new LiteralText("Some lore"))
                .addLoreLine(new LiteralText("More lore").formatted(Formatting.RED))
                .setCount(3)
                .setCallback((index, clickType, actionType) -> gui.close())
        );
        gui.setSlotRedirect(4, new Slot(player.getEnderChestInventory(), 0, 0, 0));
    	gui.open();
    	return 0;
    }
	
	/**
	 * Creates a directory at the given path if one doesn't already exist.
	 */
	private static Path createDir(Path p) {
		if (Files.notExists(p)) {
			try {
				Files.createDirectory(p);
			} catch (IOException e) {
				log(Level.WARN, "Couldn't create directory: "+p);
				e.printStackTrace();
				return null;
			}
		} else if (Files.isDirectory(p)) {
			log(Level.WARN, "Path is not a directory: "+p);
			return null;
		}
		return p;
	}
	
	public static MinecraftServer getCurrentServer() {
		return currentServer;
	}
	
	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void log(Level level, String message){
		LOGGER.log(level, message);
	}
}