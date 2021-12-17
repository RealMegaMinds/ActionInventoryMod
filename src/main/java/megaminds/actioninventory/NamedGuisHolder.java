package megaminds.actioninventory;

import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class NamedGuisHolder {
	private static final Map<String, NamedGuiBuilder> BUILDERS = null;
	
	public static void openGui(ServerPlayerEntity player, String name) {
		if (!BUILDERS.containsKey(name)) {
			ActionInventoryMod.log(Level.WARN, "No NamedGui with name: "+name);
			return;
		}
		BUILDERS.get(name).build(player).open();
	}
	
	public static void openEnderChest(ServerPlayerEntity openFor, UUID toOpen) {
		ServerPlayerEntity p = openFor.getServer().getPlayerManager().getPlayer(toOpen);
        openFor.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> GenericContainerScreenHandler.createGeneric9x3(syncId, inventory, p.getEnderChestInventory()), p.getName().shallowCopy().append(new LiteralText("'s ").append(new TranslatableText("container.enderchest")))));
	}
	
	public static void openInventory(ServerPlayerEntity openFor, UUID toOpen) {
		ServerPlayerEntity p = openFor.getServer().getPlayerManager().getPlayer(toOpen);
        openFor.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> GenericContainerScreenHandler.createGeneric9x6(syncId, inventory, p.getInventory()), p.getName().shallowCopy().append(new LiteralText("'s Inventory"))));
	}
	
	public static SimpleGui getGui(ServerPlayerEntity player, String name) {
		return BUILDERS.get(name).build(player);
	}
}