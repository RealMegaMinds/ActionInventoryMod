package megaminds.actioninventory.openers;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.JsonHelper;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockOpener extends BasicOpener {
	private static final String BLOCK = "block", ENTITY_TYPE = "entityType", POS = "position", TAGS = "tags";
	private static final List<BlockOpener> OPENERS = new ArrayList<>();
	
	private Block block;
	private BlockEntityType<?> entityType;
	private BlockPos pos;
	private List<Identifier> tags;

	@Override
	public BasicOpener fromJson(JsonObject obj, JsonDeserializationContext context) {
		block = obj.has(BLOCK) ? Registry.BLOCK.get(new Identifier(obj.get(BLOCK).getAsString())) : null;
		entityType = obj.has(ENTITY_TYPE) ? Registry.BLOCK_ENTITY_TYPE.get(new Identifier(obj.get(ENTITY_TYPE).getAsString())) : null;
		if (obj.has(POS)) {
			JsonArray arr = obj.get(POS).getAsJsonArray();
			pos = new BlockPos(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
		} else {
			pos = null;
		}
		tags = obj.has(TAGS) ? JsonHelper.toList(obj.get(TAGS).getAsJsonArray(), e->new Identifier(e.getAsString())) : null;
		return this;
	}
	
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
	
	private boolean checkEntity(Object o) {
		return !(o instanceof BlockEntity e) || Helper.nullOrEquals(entityType, e.getType());
	}
	
	private boolean checkBlock(Object o, Object bPos) {
		return !(o instanceof Block b) || Helper.nullOrEquals(block, b) && Helper.nullOrEquals(pos, bPos) && BlockTags.getTagGroup().getTagsFor(b).containsAll(tags);
	}

	@Override
	public Opener getType() {
		return Opener.BLOCK;
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