package megaminds.testmod.inventory.storable;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.text.Text;

public class Serializer {
	private static final Gson GSON;
	
	public static String toJson(StoredActionInventory requirement) {
		return GSON.toJson(requirement);
	}

	@Nullable
	public static StoredActionInventory fromJson(String json) {
		return GSON.fromJson(json, StoredActionInventory.class);
	}
	
	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.disableHtmlEscaping();
		gsonBuilder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
		gsonBuilder.registerTypeHierarchyAdapter(Text.class, new Text.Serializer());
		GSON = gsonBuilder.create();
	}
}