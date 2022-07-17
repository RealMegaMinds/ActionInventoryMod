package megaminds.actioninventory.openers;

import java.util.Optional;
import java.util.Set;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.misc.Enums.TagOption;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@PolyName("Block")
public final class BlockOpener extends BasicOpener {
	private static final Identifier TYPE = new Identifier(ActionInventoryMod.MOD_ID, "block");

	private Block block;
	private BlockPos position;
	private Optional<BlockEntityType<?>> entityType;
	private Set<Identifier> tags;
	private TagOption tagOption;

	public BlockOpener() {}

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
		if (checkEntity((BlockEntity)context[2]) && checkBlock((BlockState)context[0], (BlockPos)context[1])) {
			return super.open(player, context);
		}
		return false;
	}

	private boolean checkBlock(BlockState b, BlockPos bPos) {
		return (block==null||block.equals(b.getBlock()))
				&& (position==null||position.equals(bPos)) 
				&& (tags==null||tagOption.matches(tags, b.streamTags()));
	}

	private boolean checkEntity(BlockEntity be) {
		return entityType==null || entityType.isEmpty()&&be==null || entityType.isPresent()&&be!=null&&entityType.get()==be.getType();
	}

	public static boolean tryOpen(ServerPlayerEntity p, BlockState b, BlockPos bp, BlockEntity be) {
		return Helper.getFirst(ActionInventoryMod.OPENER_LOADER.getOpeners(TYPE), o->o.open(p, b, bp, be))!=null;
	}

	public static void registerCallbacks() {
		UseBlockCallback.EVENT.register((p,w,h,r)-> !w.isClient&&BlockOpener.tryOpen((ServerPlayerEntity)p, w.getBlockState(r.getBlockPos()), r.getBlockPos(), w.getBlockEntity(r.getBlockPos())) ? ActionResult.SUCCESS : ActionResult.PASS);
		AttackBlockCallback.EVENT.register((p,w,h,b,d)-> !w.isClient&&BlockOpener.tryOpen((ServerPlayerEntity)p, w.getBlockState(b), b, w.getBlockEntity(b)) ? ActionResult.SUCCESS : ActionResult.PASS);		
	}

	@Override
	public void validate() {
		super.validate();
		if (tagOption==null) tagOption = TagOption.ALL;
	}

	@Override
	public Identifier getType() {
		return TYPE;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public BlockPos getPosition() {
		return position;
	}

	public void setPosition(BlockPos position) {
		this.position = position;
	}

	public Optional<BlockEntityType<?>> getEntityType() {
		return entityType;
	}

	public void setEntityType(Optional<BlockEntityType<?>> entityType) {
		this.entityType = entityType;
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