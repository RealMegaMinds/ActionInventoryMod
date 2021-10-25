package megaminds.testmod.inventory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonSyntaxException;

import megaminds.testmod.TestMod;
import megaminds.testmod.inventory.openers.Opener.ClickType;
import megaminds.testmod.inventory.openers.Opener.What;
import megaminds.testmod.inventory.requirements.RequirementStorageManager;
import net.minecraft.server.network.ServerPlayerEntity;

public class ActionManager {
	private static final String INVENTORY_FOLDER = "action inventories";
	private static final Map<String, ActionInventory> allInventories = new HashMap<>();
	private static final Map<ServerPlayerEntity, ActionInventory> openInventories = new HashMap<>();

	public static ActionInventory getInventory(String name) {
		return allInventories.get(name);
	}
	
	public static Stream<String> getCommandInventoryNames() {
		return allInventories.values().stream().filter(i->i.canOpen(null, What.Command, null)).map(i->i.getName());
	}
	
	public static void onCreate(ActionInventory inv) {
		if (inv==null) return;
		allInventories.put(inv.getName(), inv);
	}
	public static void onOpen(ServerPlayerEntity p, ActionInventory inv) {
		openInventories.put(p, inv);
	}
	public static void onClose(ServerPlayerEntity p, ActionInventory inv) {
		openInventories.remove(p, inv);
	}
	
	public static boolean notify(ServerPlayerEntity player, ClickType click, What what, Object arg) {
		for (ActionInventory inv : allInventories.values()) {
			if (notify(inv, player, click, what, arg)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean notify(ActionInventory inv, ServerPlayerEntity player, ClickType click, What what, Object arg) {
		if (inv==null) {
			System.out.println("inventory is null");
		}
		if (inv!=null && inv.canOpen(click, what, arg)) {
			player.openHandledScreen(new ActionScreenHandlerFactory(inv));
			return true;
		}
		return false;
	}
	
	public static void onShutDown(Path worldFolder) {
		RequirementStorageManager.onShutdown(worldFolder.resolveSibling(INVENTORY_FOLDER));
	}

	public static void onStartUp(Path worldFolder) {
		allInventories.clear();
		openInventories.clear();
		Path invFolder = worldFolder.resolveSibling(INVENTORY_FOLDER);
		try {
			if (Files.exists(invFolder)&&Files.isDirectory(invFolder)) {
				TestMod.info("Loading ActionInventories");
				Stream<Path> files = Files.list(invFolder).filter(p->p.toString().endsWith(".json"));
				int count = 0;
				for (Path p : files.toArray(Path[]::new)) {
					try {
						onCreate(ActionJsonHelper.fromJson(Files.readString(p)));
						count++;
					} catch (JsonSyntaxException je) {
						TestMod.log(Level.WARN, "Couldn't read json: "+p);
						je.printStackTrace();
					} catch (IOException e) {
						TestMod.log(Level.WARN, "Couldn't read file: "+p);
					}
				}
				TestMod.info("Loaded "+count+" ActionInventories");
			} else if (Files.exists(invFolder)) {
				TestMod.log(Level.WARN, "Inventory file must be a directory");
			} else {
				Files.createDirectories(invFolder);
			}
		} catch (IOException e) {
			TestMod.log(Level.WARN, "Error loading ActionInventories");
		}
		RequirementStorageManager.onStartup(invFolder);
	}
}