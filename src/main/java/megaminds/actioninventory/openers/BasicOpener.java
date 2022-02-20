package megaminds.actioninventory.openers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.loaders.NamedGuiLoader;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Poly
public abstract sealed class BasicOpener implements Validated permits BlockOpener, EntityOpener, ItemOpener {
	private Identifier guiName;
	
	public boolean open(ServerPlayerEntity player, Object... context) {	//NOSONAR Used by subclasses
		return NamedGuiLoader.openGui(player, guiName, null);
	}
	
	@Override
	public void validate() {
		Validated.validate(guiName!=null, "Openers require guiName to not be null.");
	}
}