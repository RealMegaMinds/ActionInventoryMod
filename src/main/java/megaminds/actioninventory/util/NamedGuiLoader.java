package megaminds.actioninventory.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.virtual.inventory.VirtualInventory;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.gui.NamedGuiBuilder;
import megaminds.actioninventory.gui.VirtualPlayerInventory;
import megaminds.actioninventory.serialization.Serializer;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class NamedGuiLoader {
	private static final Map<String, NamedGuiBuilder> BUILDERS = new HashMap<>();
	
	public static Inventory getGuiInv(ServerPlayerEntity player, String name) {
		return Helper.ifNotNullGet(getGui(player, name), VirtualInventory::new);
	}
		
	public static boolean openGui(ServerPlayerEntity player, String name) {
		return Helper.notNullAnd(getGui(player, name), SimpleGui::open);
	}
	
	public static void openEnderChest(ServerPlayerEntity openFor, UUID toOpen) {
		ServerPlayerEntity p = openFor.getServer().getPlayerManager().getPlayer(toOpen);
        openFor.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> GenericContainerScreenHandler.createGeneric9x3(syncId, inventory, p.getEnderChestInventory()), p.getName().shallowCopy().append(new LiteralText("'s ").append(new TranslatableText("container.enderchest")))));
	}
	
	public static void openInventory(ServerPlayerEntity openFor, UUID toOpen) {
		ServerPlayerEntity p = openFor.getServer().getPlayerManager().getPlayer(toOpen);
		SimpleGui gui = new VirtualPlayerInventory(openFor, false, p);
		gui.open();
	}
	
	public static SimpleGui getGui(ServerPlayerEntity player, String name) {
		if (!BUILDERS.containsKey(name)) {
			ActionInventoryMod.warn("No NamedGui with name: "+name);
			return null;
		}
		return BUILDERS.get(name).build(player);
	}
	
	public static boolean addBuilder(NamedGuiBuilder builder) {
		String name = builder.getName();
		if (BUILDERS.containsKey(name)) {
			return false;
		} else {
			BUILDERS.put(name, builder);
			return true;
		}
	}
	
	public static void removeBuilder(String name) {
		BUILDERS.remove(name);
	}

	public static void load(Path[] paths) {
		for (Path p : paths) {
			load(p);
		}
	}
	
	public static int[] load(Path path) {
		int[] count = new int[2];
		try (Stream<Path> files = Files.list(path)) {
			files.filter(p->p.toString().endsWith(".json"))
			.forEach(p->{
				try (BufferedReader br = Files.newBufferedReader(p)) {
					NamedGuiBuilder builder = Serializer.builderFromJson(br);
					if (addBuilder(builder)) {
						count[0]++;
					} else {
						ActionInventoryMod.warn("A NamedGuiBuilder with name: '"+builder.getName()+"' already exists.");
						count[1]++;
					}
				} catch (IOException e) {
					ActionInventoryMod.warn("Failed to read NamedGuiBuilder from: "+p);
					count[1]++;
				}
			});
		} catch (IOException e) {
			ActionInventoryMod.warn("Cannot read files from: "+path);
		}
		ActionInventoryMod.info("Loaded "+count[0]+" NamedGuiBuilders. Failed to load "+count[1]+". ("+path+")");
		return count;
	}
	
	public static void clear() {
		BUILDERS.clear();
	}
}