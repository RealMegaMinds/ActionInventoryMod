package megaminds.actioninventory.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import eu.pb4.sgui.api.gui.SimpleGui;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.gui.NamedGuiBuilder;
import megaminds.actioninventory.gui.VirtualPlayerInventory;
import megaminds.actioninventory.serialization.Serializer;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.ValidationException;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class NamedGuiLoader {
	private static final Map<Identifier, NamedGuiBuilder> BUILDERS = new HashMap<>();

	private NamedGuiLoader() {}

	public static boolean openGui(ServerPlayerEntity player, Identifier name) {
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

	public static SimpleGui getGui(ServerPlayerEntity player, Identifier name) {
		if (!BUILDERS.containsKey(name)) {
			ActionInventoryMod.warn("No NamedGui with name: "+name);
			return null;
		}
		return BUILDERS.get(name).build(player);
	}

	public static boolean addBuilder(NamedGuiBuilder builder) {
		Identifier name = builder.getName();
		if (BUILDERS.containsKey(name)) {
			ActionInventoryMod.warn("A NamedGuiBuilder with name: '"+builder.getName()+"' already exists.");
			return false;
		} else {
			BUILDERS.put(name, builder);
			return true;
		}
	}

	public static void removeBuilder(Identifier name) {
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
			Path[] paths = files.filter(p->p.toString().endsWith(".json")).toArray(Path[]::new);
			for (Path p : paths) {
				load(p, count);
			}
		} catch (IOException e) {
			ActionInventoryMod.warn("Cannot read files from: "+path);
		}
		ActionInventoryMod.info("Loaded "+count[0]+" NamedGuiBuilders. Failed to load "+count[1]+". ("+path+")");
		return count;
	}
	
	private static void load(Path path, int[] count) {
		try (BufferedReader br = Files.newBufferedReader(path)) {
			NamedGuiBuilder builder = loadBuilder(br);
			if (builder!=null && addBuilder(builder)) {
				count[0]++;
			} else {
				count[1]++;
			}
		} catch (IOException e) {
			ActionInventoryMod.warn("Failed to read NamedGuiBuilder from: "+path);
			count[1]++;
		}
	}
	
	private static NamedGuiBuilder loadBuilder(BufferedReader br) {
		try {
			return Serializer.builderFromJson(br);
		} catch (ValidationException e) {
			ActionInventoryMod.warn("NamedGuiBuilder Validation Exception: "+e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void clear() {
		BUILDERS.clear();
	}
}