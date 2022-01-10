package megaminds.actioninventory.openers;

import java.util.Objects;

import com.google.gson.annotations.JsonAdapter;

import megaminds.actioninventory.serialization.PolymorphicTypeAdapterFactory;
import megaminds.actioninventory.util.NamedGuiLoader;
import net.minecraft.server.network.ServerPlayerEntity;

@JsonAdapter(PolymorphicTypeAdapterFactory.class)
public abstract class BasicOpener {
	private String name;
	
	public boolean open(ServerPlayerEntity player, Object... context) {
		return NamedGuiLoader.openGui(player, name);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = Objects.requireNonNull(name);
	}
	
	public abstract boolean addToMap();
	public abstract void removeFromMap();
}