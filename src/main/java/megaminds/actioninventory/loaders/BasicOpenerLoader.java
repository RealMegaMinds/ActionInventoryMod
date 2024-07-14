package megaminds.actioninventory.loaders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.openers.BasicOpener;
import megaminds.actioninventory.util.JsonHelper;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class BasicOpenerLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	private static final Identifier LOADER_ID = new Identifier(ActionInventoryMod.MOD_ID, "openers");
	private final Map<Identifier, List<BasicOpener>> openers = new HashMap<>();

	public BasicOpenerLoader() {
		super(JsonHelper.DEFAULT_GSON, ActionInventoryMod.MOD_ID+"/openers");
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		openers.clear();
		prepared.forEach((id, json) -> loadOpener(id.toString(), json));
		ActionInventoryMod.info("Loaded "+openers.size()+" Openers.");
	}

	private void validate(BasicOpener opener) {
		//TODO Replace ValidationException with this (Look at ServerAdvancementLoader for example)
	}

	public boolean loadOpener(String id, JsonElement json) {
		try {
			var opener = ActionInventoryMod.getGson().fromJson(json, BasicOpener.class);
			validate(opener);
			openers.computeIfAbsent(opener.getType(), t->new ArrayList<>()).add(opener);
		} catch (Exception e) {
			ActionInventoryMod.LOGGER.error("Parsing error loading Opener {}: {}", id, e);
			return false;
		}
		return true;
	}

	public List<BasicOpener> getOpeners(Identifier type) {
		var list = openers.get(type);
		return list!=null ? list : Collections.emptyList();
	}

	@Override
	public Identifier getFabricId() {
		return LOADER_ID;
	}
}