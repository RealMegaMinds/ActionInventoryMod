package megaminds.actioninventory.openers;

import java.util.Set;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.misc.Enums.TagOption;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

@PolyName("Item")
public final class ItemOpener extends BasicOpener {
	private static final Identifier TYPE = new Identifier(ActionInventoryMod.MOD_ID, "item");

	private ItemPredicate itemPredicate;
	private Set<Identifier> tags;
	private TagOption tagOption;

	public ItemOpener(Identifier guiName, ItemPredicate itemPredicate, Set<Identifier> tags, TagOption tagOption) {
		super(guiName);
		this.itemPredicate = itemPredicate;
		this.tags = tags;
		this.tagOption = tagOption;
	}

	@Override
	public boolean open(ServerPlayerEntity player, Object... context) {
		var s = (ItemStack) context[0];
		System.out.println(s.getItem());
		System.out.println(itemPredicate.items());
		if (itemPredicate.test(s) && (tags==null || tagOption.matches(tags, s.streamTags()))) {
			return super.open(player, context);
		}
		return false;
	}

	public static boolean tryOpen(ServerPlayerEntity p, ItemStack s) {
		return Helper.getFirst(ActionInventoryMod.OPENER_LOADER.getOpeners(TYPE), o->o.open(p, s))!=null;
	}

	public static void registerCallbacks() {
		UseItemCallback.EVENT.register((p,w,h)->
		!w.isClient&&ItemOpener.tryOpen((ServerPlayerEntity)p, p.getStackInHand(h)) ? TypedActionResult.success(ItemStack.EMPTY) : TypedActionResult.pass(ItemStack.EMPTY));		
	}

	@Override
	public void validate() {
		super.validate();
		if (itemPredicate==null) itemPredicate = Helper.ANY_ITEM;
		if (tagOption==null) tagOption = TagOption.ALL;
	}

	@Override
	public Identifier getType() {
		return TYPE;
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