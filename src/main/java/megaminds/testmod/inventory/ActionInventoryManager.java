package megaminds.testmod.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import megaminds.testmod.inventory.OpenChecker.ClickType;
import megaminds.testmod.inventory.OpenChecker.OpenType;
import net.minecraft.server.network.ServerPlayerEntity;

public class ActionInventoryManager {
	private static Map<String, ActionInventory> ALL_INVENTORIES = new HashMap<>();	//TODO probably should initialize this from files
	private static Map<ServerPlayerEntity, ActionInventory> OPEN_INVENTORIES = new HashMap<>();
	
	public static void addInventory(ActionInventory inventory) {
		if (inventory == null) return;
		ALL_INVENTORIES.put(inventory.getName(), inventory);
	}
	
	public static ActionInventory getInventory(String name) {
		return ALL_INVENTORIES.get(name);
	}
	
	public static Stream<String> getOpenInventoryNames() {
		return ALL_INVENTORIES.values().stream().filter(i->i.getOpenChecker().check(OpenType.COMMAND, null, null)).map(i->i.getName());
	}
	
	public static boolean notify(ServerPlayerEntity player, OpenType open, ClickType click, Object arg) {
		return ALL_INVENTORIES.values().stream().anyMatch(i -> i.getOpenChecker().check(open, click, arg) && open(i, player));
	}
	
	public static boolean open(String inventory, ServerPlayerEntity player) {
		return open(getInventory(inventory), player);
	}
	
	public static void onClose(ServerPlayerEntity player, ActionInventory inventory) {
		OPEN_INVENTORIES.remove(player, inventory);
	}
	
	public static void onOpen(ServerPlayerEntity player, ActionInventory inventory) {
		OPEN_INVENTORIES.put(player, inventory);
	}
	
	public static boolean open(ActionInventory inventory, ServerPlayerEntity player) {
		if (inventory==null) return false;
		
		((ServerPlayerEntity)player).openHandledScreen(inventory);
		return true;
	}
}