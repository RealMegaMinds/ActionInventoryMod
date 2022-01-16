package megaminds.actioninventory.openers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.Setter;
import lombok.Getter;
import megaminds.actioninventory.util.annotations.Poly;
import megaminds.actioninventory.misc.ItemStackish;
import megaminds.actioninventory.misc.Enums.TagOption;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.Helper;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

@Getter
@Setter
@Poly("Item")
public final class ItemOpener extends BasicOpener {
	private static final List<ItemOpener> OPENERS = new ArrayList<>();

	private ItemStackish stack;
	private Set<Identifier> tags;
	private TagOption tagOption;
	
	public ItemOpener(Identifier guiName, ItemStackish stack, Set<Identifier> tags, TagOption tagOption) {
		super(guiName);
		this.stack = stack;
		this.tags = tags;
		this.tagOption = tagOption;
	}

	@Override
	public boolean open(ServerPlayerEntity player, Object... context) {
		ItemStack s = (ItemStack) context[0];
		if ((stack==null || stack.specifiedEquals(s)) && (tags==null || tagOption.matches(tags, ItemTags.getTagGroup().getTagsFor(s.getItem())))) {
			return super.open(player, context);
		}
		return false;
	}
		
	public static boolean tryOpen(ServerPlayerEntity p, ItemStack s) {
		return Helper.getFirst(OPENERS, o->o.open(p, s))!=null;
	}

	public static void registerCallbacks() {
		UseItemCallback.EVENT.register((p,w,h)->
			!w.isClient&&ItemOpener.tryOpen((ServerPlayerEntity)p, p.getStackInHand(h)) ? TypedActionResult.success(ItemStack.EMPTY) : TypedActionResult.pass(ItemStack.EMPTY)
		);		
	}
	
	public static void clearOpeners() {
		OPENERS.clear();
	}
	
	@Override
	public void validate() {
		super.validate();
		if (tagOption==null) tagOption = TagOption.ALL;
		Validated.validate(!OPENERS.contains(this) && OPENERS.add(this), "Failed to add Block opener to list.");
	}
}