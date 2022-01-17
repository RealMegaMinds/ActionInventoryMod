package megaminds.actioninventory.openers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.misc.Enums.TagOption;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Getter
@Setter
@NoArgsConstructor
@PolyName("Block")
public final class BlockOpener extends BasicOpener {
	private static final List<BlockOpener> OPENERS = new ArrayList<>();
	
	private Block block;
	private BlockPos position;
	private Optional<BlockEntityType<?>> entityType;
	private Set<Identifier> tags;
	private TagOption tagOption;

	public BlockOpener(Identifier guiName, Block block, BlockPos position, Optional<BlockEntityType<?>> entityType, Set<Identifier> tags, TagOption tagOption) {
		super(guiName);
		this.block = block;
		this.position = position;
		this.entityType = entityType;
		this.tags = tags;
		this.tagOption = tagOption;
	}

	/**
	 * context[0] = Block <br>
	 * context[1] = BlockPos <br>
	 * context[2] = BlockEntity
	 */
	@Override
	public boolean open(ServerPlayerEntity player, Object... context) {
		if (checkEntity((BlockEntity)context[2]) && checkBlock((Block)context[0], (BlockPos)context[1])) {
			return super.open(player, context);
		}
		return false;
	}
	
	private boolean checkBlock(Block b, BlockPos bPos) {
		return (block==null||block.equals(b)) 
				&& (position==null||position.equals(bPos)) 
				&& (tags==null||tagOption.matches(tags, BlockTags.getTagGroup().getTagsFor(b)));
	}

	private boolean checkEntity(BlockEntity be) {
		return entityType==null || entityType.isEmpty()&&be==null || entityType.isPresent()&&be!=null&&entityType.get()==be.getType();
	}
	
	public static boolean tryOpen(ServerPlayerEntity p, Block b, BlockPos bp, BlockEntity be) {
		return Helper.getFirst(OPENERS, o->o.open(p, b, bp, be))!=null;
	}

	public static void registerCallbacks() {
		UseBlockCallback.EVENT.register((p,w,h,r)->
			!w.isClient&&BlockOpener.tryOpen((ServerPlayerEntity)p, w.getBlockState(r.getBlockPos()).getBlock(), r.getBlockPos(), w.getBlockEntity(r.getBlockPos())) ? ActionResult.SUCCESS : ActionResult.PASS
		);
		AttackBlockCallback.EVENT.register((p,w,h,b,d)->
			!w.isClient&&BlockOpener.tryOpen((ServerPlayerEntity)p, w.getBlockState(b).getBlock(), b, w.getBlockEntity(b)) ? ActionResult.SUCCESS : ActionResult.PASS
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