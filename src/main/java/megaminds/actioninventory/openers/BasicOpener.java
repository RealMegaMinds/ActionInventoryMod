package megaminds.actioninventory.openers;

import java.util.Objects;

import com.google.gson.annotations.JsonAdapter;

import megaminds.actioninventory.loaders.NamedGuiLoader;
import megaminds.actioninventory.misc.Validated;
import megaminds.actioninventory.serialization.PolymorphicTypeAdapterFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@JsonAdapter(PolymorphicTypeAdapterFactory.class)
public abstract sealed class BasicOpener implements Validated permits BlockOpener, EntityOpener, ItemOpener {
	private Identifier name;
	
	public boolean open(ServerPlayerEntity player, Object... context) {	//NOSONAR Used by subclasses
		return NamedGuiLoader.openGui(player, name);
	}
	
	public Identifier getName() {
		return name;
	}

	public void setName(Identifier name) {
		this.name = Objects.requireNonNull(name);
	}
	
	@Override
	public void validate() {
		Validated.validate(name!=null, "Openers require name to not be null.");
	}
}