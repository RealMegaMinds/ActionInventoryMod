package megaminds.actioninventory.openers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.Constants.TagOption;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BlockOpener extends BasicOpener {
	private static final List<BlockOpener> OPENERS = new ArrayList<>();
	
	private Block block;
	private BlockPos position;
	@Nullable private Optional<BlockEntityType<?>> entityType;
	@Nullable private Optional<Set<Identifier>> tags;
	private TagOption tagOption = TagOption.ALL;

	/**
	 * context[0] = Block <br>
	 * context[1] = BlockPos <br>
	 * context[2] = BlockEntity
	 */
	@Override
	public boolean open(ServerPlayerEntity player, Object... context) {
		if (checkEntity(context[2]) && checkBlock(context[0], context[1])) {
			return super.open(player, context);
		}
		return false;
	}
	
	private boolean checkBlock(Object o, Object bPos) {
		if  (block==null||block.equals(o) && position==null||position.equals(bPos)) {
			if (tags==null) return true;
			
			Collection<Identifier> c = BlockTags.getTagGroup().getTagsFor((Block)o);
			switch (tagOption) {
			case ALL:
				return c.containsAll(tags.get());
			case ANY:
				break;
			case EXACT:
				List<Identifier> test = tags.get();
				if (c.size()!=test.size()) return false;
				
				List<Identifier> real = List.copyOf(c);
				for (Identifier i : test) {
					real.remove(c);
				}
				return real.isEmpty();
			case NONE:
				break;
			}
		}
		return false;
	}

	private boolean checkEntity(Object o) {
		return !(o instanceof BlockEntity e) || entityType.filter(t->!t.equals(e.getType())).isEmpty();
	}
	
	public boolean addToMap() {
		return OPENERS.contains(this) || OPENERS.add(this);
	}
	
	public void removeFromMap() {
		OPENERS.remove(this);
	}
	
	public static boolean tryOpen(ServerPlayerEntity p, Block b, BlockPos bp, BlockEntity be) {
		return Helper.getFirst(OPENERS, o->o.open(p, b, bp, be))!=null;
	}
}