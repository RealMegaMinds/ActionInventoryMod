package megaminds.actioninventory.consumables;

import java.util.Set;
import java.util.UUID;

import megaminds.actioninventory.misc.ItemStackish;
import megaminds.actioninventory.misc.Enums.TagOption;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

@PolyName("Item")
public final class ItemConsumable extends IntConsumable {
	private ItemStackish stack;
	private Set<Identifier> tags;
	private TagOption tagOption;

	public ItemConsumable() {}
	
	public ItemConsumable(ItemStackish stack, Set<Identifier> tags, TagOption tagOption) {
		this.stack = stack;
		this.tags = tags;
		this.tagOption = tagOption;
	}

	public ItemConsumable(int amount, ItemStackish stack, Set<Identifier> tags, TagOption tagOption) {
		super(amount);
		this.stack = stack!=null ? stack : ItemStackish.MATCH_ALL;
		this.tags = tags;
		this.tagOption = tagOption!=null ? tagOption : TagOption.ANY;
	}

	@Override
	public boolean canConsumeFull(MinecraftServer server, UUID player, int left) {
		var p = Helper.getPlayer(server, player);
		var inv = p.getInventory();

		for (int i = 0, size = inv.size(); i < size; i++) {
			var s = inv.getStack(i);
			if (stack.specifiedEquals(s) && (tags==null || tagOption.matches(tags, s.streamTags()))) {
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
			if (stack.specifiedEquals(s) && (tags==null || tagOption.matches(tags, s.streamTags()))) {
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

	public ItemStackish getStack() {
		return stack;
	}

	public void setStack(ItemStackish stack) {
		this.stack = stack;
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