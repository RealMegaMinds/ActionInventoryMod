package megaminds.actioninventory.loaders;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.gui.ActionInventoryBuilder;
import megaminds.actioninventory.gui.VirtualPlayerInventory;
import megaminds.actioninventory.serialization.Serializer;
import megaminds.actioninventory.util.ValidationException;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ActionInventoryLoader implements SimpleSynchronousResourceReloadListener {
	private static final Identifier LOADER_ID = new Identifier(ActionInventoryMod.MOD_ID, "inventories");

	private final Map<Identifier, ActionInventoryBuilder> builders = new HashMap<>();

	@Override
	public void reload(ResourceManager manager) {
		builders.clear();

		var count = new int[2];
		var resources = Map.copyOf(manager.findResources(ActionInventoryMod.MOD_ID+"/inventories", s->s.getPath().endsWith(".json")));
		for (var resource : resources.entrySet()) {
			try (var res = resource.getValue().getInputStream()) {
				var builder = Serializer.builderFromJson(new InputStreamReader(res));
				addBuilder(builder);
				count[0]++;	//success
				continue;
			} catch (ValidationException e) {
				ActionInventoryMod.warn("Action Inventory Validation Exception: "+e.getMessage());
			} catch (IOException e) {
				ActionInventoryMod.warn("Failed to read Action Inventory from: "+resource.getKey());
			}
			count[1]++;	//fail
		}
		ActionInventoryMod.info("Loaded "+count[0]+" Action Inventories. Failed to load "+count[1]+".");
	}

	@Override
	public Identifier getFabricId() {
		return LOADER_ID;
	}

	public void openEnderChest(ServerPlayerEntity openFor, UUID toOpen) {
		var p = openFor.getServer().getPlayerManager().getPlayer(toOpen);
		openFor.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> GenericContainerScreenHandler.createGeneric9x3(syncId, inventory, p.getEnderChestInventory()), p.getName().copy().append(Text.of("'s ")).append(Text.translatable("container.enderchest"))));
	}

	public void openInventory(ServerPlayerEntity openFor, UUID toOpen) {
		var p = openFor.getServer().getPlayerManager().getPlayer(toOpen);
		new VirtualPlayerInventory(openFor, false, p).open();
	}

	public ActionInventoryBuilder getBuilder(Identifier name) {
		return builders.get(name);
	}

	public void addBuilder(ActionInventoryBuilder builder) {
		builders.put(builder.getName(), builder);
	}

	public void removeBuilder(Identifier name) {
		builders.remove(name);
	}

	public boolean hasBuilder(Identifier name) {
		return builders.containsKey(name);
	}

	public Set<Identifier> builderNames() {
		return Set.copyOf(builders.keySet());
	}

	public Set<Identifier> builderNames(Predicate<ActionInventoryBuilder> filter) {
		return builders.entrySet().stream().filter(e -> filter.test(e.getValue())).map(Entry::getKey).collect(Collectors.toUnmodifiableSet());
	}
}