package megaminds.actioninventory.consumables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.misc.ItemStackish;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PolyName("Item")
public final class ItemConsumable extends BasicConsumable {
	private ItemStackish stack;

	@Override
	public void validate() {
		Advancement s;
		AbstractCriterionConditions
	}

	@Override
	public boolean canConsumeFull(ServerPlayerEntity player, NbtElement storage) {
		return false;
	}

	@Override
	public NbtElement consume(ServerPlayerEntity player, NbtElement storage, boolean saveAmount) {
		return null;
	}

	@Override
	public String getStorageName() {
		return "Item";
	}

	@Override
	public BasicConsumable copy() {
		return new ItemConsumable(stack);
	}
}