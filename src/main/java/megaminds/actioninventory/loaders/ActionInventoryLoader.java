package megaminds.actioninventory.loaders;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.gui.ActionInventoryBuilder;
import megaminds.actioninventory.gui.VirtualPlayerInventory;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class ActionInventoryLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	private static final Identifier LOADER_ID = new Identifier(ActionInventoryMod.MOD_ID, "inventories");
	private static final Gson GSON = new Gson();
	private final Map<Identifier, ActionInventoryBuilder> builders = new HashMap<>();

	public ActionInventoryLoader() {
		super(GSON, ActionInventoryMod.MOD_ID+"/inventories");
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		builders.clear();
		prepared.forEach((id, json) -> loadBuilder(id.toString(), json).ifPresent(this::addBuilder));
		ActionInventoryMod.info("Loaded "+builders.size()+" Action Inventories.");
	}

	private void validate(ActionInventoryBuilder builder) {
		//TODO Replace ValidationException with this (Look at ServerAdvancementLoader for example)
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

	public Optional<ActionInventoryBuilder> loadBuilder(String id, JsonElement json) {
		try {
			var builder = ActionInventoryMod.getGson().fromJson(json, ActionInventoryBuilder.class);
			validate(builder);
			return Optional.of(builder);
		} catch (Exception e) {
			ActionInventoryMod.LOGGER.error("Parsing error loading Action Inventory {}: {}", id, e);
			return Optional.empty();
		}
	}
	
	public Optional<ActionInventoryBuilder> loadBuilder(String id, Reader json) {
		try {
			var builder = ActionInventoryMod.getGson().fromJson(json, ActionInventoryBuilder.class);
			validate(builder);
			return Optional.of(builder);
		} catch (Exception e) {
			ActionInventoryMod.LOGGER.error("Parsing error loading Action Inventory {}: {}", id, e);
			return Optional.empty();
		}
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