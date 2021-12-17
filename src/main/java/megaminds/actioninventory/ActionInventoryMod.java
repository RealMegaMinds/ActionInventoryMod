package megaminds.actioninventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
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
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.AnimatedGuiElement;
import eu.pb4.sgui.api.elements.AnimatedGuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import megaminds.actioninventory.util.ActionObjectHandler;
import megaminds.actioninventory.util.Helper;

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
		Helper.logger = LOGGER;

		CommandRegistrationCallback.EVENT.register((d,s)->{
			d.register(CommandManager.literal("test").then(CommandManager.argument("inputText", TextArgumentType.text()).executes(ActionInventoryMod::test)));
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
		SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false) {
			@Override
			public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
				this.player.sendMessage(new LiteralText(type.toString()), false);

				return super.onClick(index, type, action, element);
			}

			@Override
			public void onTick() {
				this.setSlot(0, new GuiElementBuilder(Items.ARROW).setCount((int) (player.world.getTime() % 127)));
				super.onTick();
			}
		};

		gui.setTitle(new LiteralText("Nice"));
		gui.setSlot(0, new GuiElementBuilder(Items.ARROW).setCount(100));
		gui.setSlot(1, new AnimatedGuiElement(new ItemStack[]{
				Items.NETHERITE_PICKAXE.getDefaultStack(),
				Items.DIAMOND_PICKAXE.getDefaultStack(),
				Items.GOLDEN_PICKAXE.getDefaultStack(),
				Items.IRON_PICKAXE.getDefaultStack(),
				Items.STONE_PICKAXE.getDefaultStack(),
				Items.WOODEN_PICKAXE.getDefaultStack()
		}, 10, false, (x, y, z) -> {
		}));

		gui.setSlot(2, new AnimatedGuiElementBuilder()
				.setItem(Items.NETHERITE_AXE).setDamage(150).saveItemStack()
				.setItem(Items.DIAMOND_AXE).setDamage(150).unbreakable().saveItemStack()
				.setItem(Items.GOLDEN_AXE).glow().saveItemStack()
				.setItem(Items.IRON_AXE).enchant(Enchantments.AQUA_AFFINITY, 1).hideFlags().saveItemStack()
				.setItem(Items.STONE_AXE).saveItemStack()
				.setItem(Items.WOODEN_AXE).saveItemStack()
				.setInterval(10).setRandom(true)
				);

		for (int x = 3; x < gui.getSize(); x++) {
			ItemStack itemStack = Items.STONE.getDefaultStack();
			itemStack.setCount(x);
			gui.setSlot(x, new GuiElement(itemStack, (index, clickType, actionType) -> {
			}));
		}

		gui.setSlot(5, new GuiElementBuilder(Items.PLAYER_HEAD)
				.setSkullOwner(
						"ewogICJ0aW1lc3RhbXAiIDogMTYxOTk3MDIyMjQzOCwKICAicHJvZmlsZUlkIiA6ICI2OTBkMDM2OGM2NTE0OGM5ODZjMzEwN2FjMmRjNjFlYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ5emZyXzciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI0OGVhYTQxNGNjZjA1NmJhOTY5ZTdkODAxZmI2YTkyNzhkMGZlYWUxOGUyMTczNTZjYzhhOTQ2NTY0MzU1ZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
						null, null)
				.setName(new LiteralText("Battery"))
				.glow()
				);

		gui.setSlot(6, new GuiElementBuilder(Items.PLAYER_HEAD)
				.setSkullOwner(new GameProfile(UUID.fromString("f5a216d9-d660-4996-8d0f-d49053677676"), "patbox"), player.server)
				.setName(new LiteralText("Patbox's Head"))
				.glow()
				);

		gui.setSlot(7, new GuiElementBuilder()
				.setItem(Items.BARRIER)
				.glow()
				.setName(new LiteralText("Bye")
						.setStyle(Style.EMPTY.withItalic(false).withBold(true)))
				.addLoreLine(new LiteralText("Some lore"))
				.addLoreLine(new LiteralText("More lore").formatted(Formatting.RED))
				.setCount(3)
				.setCallback((index, clickType, actionType) -> gui.close())
				);

		gui.setSlot(8, new GuiElementBuilder()
				.setItem(Items.TNT)
				.glow()
				.setName(new LiteralText("Test :)")
						.setStyle(Style.EMPTY.withItalic(false).withBold(true)))
				.addLoreLine(new LiteralText("Some lore"))
				.addLoreLine(new LiteralText("More lore").formatted(Formatting.RED))
				.setCount(1)
				.setCallback((index, clickType, actionType) -> {
					player.sendMessage(new LiteralText("derg "), false);
					ItemStack item = gui.getSlot(index).getItemStack();
					if (clickType == ClickType.MOUSE_LEFT) {
						item.setCount(item.getCount() == 1 ? item.getCount() : item.getCount() - 1);
					} else if (clickType == ClickType.MOUSE_RIGHT) {
						item.setCount(item.getCount() + 1);
					}
					((GuiElement) gui.getSlot(index)).setItemStack(item);

					if (item.getCount() <= player.getEnderChestInventory().size()) {
						gui.setSlotRedirect(4, new Slot(player.getEnderChestInventory(), item.getCount() - 1, 0, 0));
					}
				})
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