package megaminds.actioninventory.consumables;

import java.util.Set;
import java.util.UUID;

import net.minecraft.predicate.item.ItemPredicate;
import megaminds.actioninventory.misc.Enums.TagOption;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

@PolyName("Item")
public final class ItemConsumable extends IntConsumable {
	private ItemPredicate itemPredicate;
	private Set<Identifier> tags;
	private TagOption tagOption;

	public ItemConsumable() {}

	public ItemConsumable(ItemPredicate itemPredicate, Set<Identifier> tags, TagOption tagOption) {
		this.itemPredicate = itemPredicate;
		this.tags = tags;
		this.tagOption = tagOption;
	}

	public ItemConsumable(int amount, ItemPredicate itemPredicate, Set<Identifier> tags, TagOption tagOption) {
		super(amount);
		this.itemPredicate = itemPredicate;
		this.tags = tags;
		this.tagOption = tagOption;
	}

	@Override
	public boolean canConsumeFull(MinecraftServer server, UUID player, int left) {
		var p = Helper.getPlayer(server, player);
		var inv = p.getInventory();

		for (int i = 0, size = inv.size(); i < size; i++) {
			var s = inv.getStack(i);
			if (itemPredicate.test(s) && (tags==null || tagOption.matches(tags, s.streamTags()))) {
				left -= s.getCount();
				if (left<=0) return true;
			}
		}
		return false;
	}

	@Override
	public int consume(MinecraftServer server, UUID player, int left) {
		var p = Helper.getPlayer(server, player);
		var inv = p.getInventory();

		for (int i = 0, size = inv.size(); i < size; i++) {
			var s = inv.getStack(i);
			if (itemPredicate.test(s) && (tags==null || tagOption.matches(tags, s.streamTags()))) {
				var count = Math.min(s.getCount(), left);
				left -= count;
				s.setCount(s.getCount()-count);
				if (left<=0) break;
			}
		}
		return left;
	}

	@Override
	public void validate() {
		Validated.validate(getAmount()>=0, "Item consumable requires amount to be 0 or above, but it is: "+getAmount());
		if (itemPredicate==null) itemPredicate = Helper.ANY_ITEM;
		if (tagOption==null) tagOption = TagOption.ANY;
	}

	@Override
	public String getStorageName() {
		return "Item";
	}

	@Override
	public BasicConsumable copy() {
		return new ItemConsumable(getAmount(), itemPredicate, tags == null ? null : Set.copyOf(tags), tagOption);
	}

	public ItemPredicate getItemPredicate() {
		return itemPredicate;
	}

	public void setItemPredicate(ItemPredicate itemPredicate) {
		this.itemPredicate = itemPredicate;
	}

	public Set<Identifier> getTags() {
		return tags;
	}

	public void setTags(Set<Identifier> tags) {
		this.tags = tags;
	}

	public TagOption getTagOption() {
		return tagOption;
	}

	public void setTagOption(TagOption tagOption) {
		this.tagOption = tagOption;
	}
}