package megaminds.actioninventory.serialization;

import java.io.Reader;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import megaminds.actioninventory.NamedGuiBuilder;
import megaminds.actioninventory.callbacks.click.BasicAction;
import net.minecraft.item.ItemStack;

public class Serializer {
	public static final Gson GSON;
	
	public static NamedGuiBuilder fromJson(Reader json) {
		return GSON.fromJson(json, NamedGuiBuilder.class);
	}
	
	static {
		GSON = new GsonBuilder()
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.registerTypeHierarchyAdapter(BasicAction.class, new BasicActionSerializer())
				.registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
				.registerTypeAdapter(NamedGuiBuilder.class, new NamedGuiBuilderSerializer())
				.registerTypeAdapter(Function.class, new SlotFunctionSerializer())
				.create();
	}
}