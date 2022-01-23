package megaminds.actioninventory.consumables;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.misc.ItemStackish;
import megaminds.actioninventory.misc.Enums.TagOption;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PolyName("Item")
public final class ItemConsumable extends NumberConsumable {
	private ItemStackish stack;
	private Set<Identifier> tags;
	private TagOption tagOption;
	
	@Override
	public boolean canConsume(ServerPlayerEntity player, long left) {
		PlayerInventory inv = player.getInventory();
		final int size = inv.size();
		int available = 0;
		for (int i = 0; i < size; i++) {
			ItemStack s = inv.getStack(i);
			
			if (stack.specifiedEquals(s) && (tags==null || tagOption.matches(tags, ItemTags.getTagGroup().getTagsFor(s.getItem())))) {
				available += Math.min(s.getCount(), left);
			}
			if (available>=left) return true;
		}
		return available>=left;
	}

	@Override
	public long consume(ServerPlayerEntity player, long left) {
		PlayerInventory inv = player.getInventory();
		final int size = inv.size();
		for (int i = 0; i < size; i++) {
			ItemStack s = inv.getStack(i);
			
			if (stack.specifiedEquals(s) 
					&& (tags==null || 
					tagOption.matches(tags, ItemTags.getTagGroup().getTagsFor(s.getItem())))) {
				int can = (int) Math.min(s.getCount(), left);
				s.setCount(s.getCount()-can);
				left-=can;
			}
			
			if (left<=0) break;
		}
		return left;
	}

	@Override
	public void validate() {
		if (stack==null) stack = ItemStackish.MATCH_ALL;
		if (tagOption==null) tagOption = TagOption.ANY;
	}

	@Override
	public String getStorageName() {
		return "Item";
	}

	@Override
	public BasicConsumable copy() {
		return new ItemConsumable(stack, Set.copyOf(tags), tagOption);
	}
}