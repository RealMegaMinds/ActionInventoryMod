package megaminds.actioninventory.loaders;

import ;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.openers.BasicOpener;
import megaminds.actioninventory.serialization.Serializer;
import megaminds.actioninventory.util.ValidationException;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class BasicOpenerLoader implements SimpleSynchronousResourceReloadListener {
	private static final Identifier LOADER_ID = new Identifier(ActionInventoryMod.MOD_ID, "openers");

	private final Map<Identifier, List<BasicOpener>> openers = new HashMap<>();

	@Override
	public void reload(ResourceManager manager) {
		openers.clear();

		var count = new int[2];
		var paths = Set.copyOf(manager.findResources(ActionInventoryMod.MOD_ID+"/openers", s->s.endsWith(".json")));
		for (var path : paths) {
			try (var res = manager.getResource(path).getInputStream()) {
				var opener = Serializer.openerFromJson(new InputStreamReader(res));
				addOpener(opener);
				count[0]++;	//success
				continue;
			} catch (ValidationException e) {
				ActionInventoryMod.warn("Opener Validation Exception: "+e.getMessage());
			} catch (IOException e) {
				ActionInventoryMod.warn("Failed to read Opener from: "+path);
			}
			count[1]++;	//fail
		}
		ActionInventoryMod.info("Loaded "+count[0]+" Openers. Failed to load "+count[1]+".");
	}

	public void addOpener(BasicOpener opener) {
		openers.computeIfAbsent(opener.getType(), t->new ArrayList<>()).add(opener);
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