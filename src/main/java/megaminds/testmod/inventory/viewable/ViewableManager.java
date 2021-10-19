package megaminds.testmod.inventory.viewable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;

import megaminds.testmod.TestMod;
import megaminds.testmod.inventory.storable.Serializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ViewableManager {
	private static final String INVENTORY_FOLDER = "inventories";
	private static final Map<Text, ViewableActionInventory> allInventories = new HashMap<>();
	private static final Map<ServerPlayerEntity, ViewableActionInventory> openInventories = new HashMap<>();

	public static void onCreate(Text name, ViewableActionInventory inv) {
		if (inv!=null) {
			allInventories.put(name, inv);
		}
	}
	public static void onOpen(ServerPlayerEntity p, ViewableActionInventory inv) {
		openInventories.put(p, inv);
	}
	public static void onClose(ServerPlayerEntity p, ViewableActionInventory inv) {
		openInventories.remove(p, inv);
	}

	public static void load(Path worldFolder) {
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
						ViewableActionInventory inv = ViewableActionInventory.create(Serializer.fromJson(Files.readString(p)));
						if (inv==null) {
							TestMod.log(Level.WARN, "Couldn't load: "+p);
						} else {
							count++;
						}
					} catch (IOException e) {
						TestMod.log(Level.WARN, "Couldn't read: "+p);
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
	}
}