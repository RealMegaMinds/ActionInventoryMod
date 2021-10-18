package megaminds.testmod.inventory.viewable;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ViewableManager {
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
	
	public static void load() {
		//TODO load inventories from file here
	}
}