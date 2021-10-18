package megaminds.testmod.inventory;

import static megaminds.testmod.inventory.OpenChecker.ArgType.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.registry.Registry;

public class OpenChecker {
	public static enum OpenType {
		BLOCK(REAL_NAME, TAG),
		BLOCK_ENTITY(NBT, POS, TYPE),
		ITEM(REAL_NAME, CUSTOM_NAME, TAG, NBT),
		ENTITY(CUSTOM_NAME, REAL_NAME, TAG, NBT, POS, TYPE, UUID, TEAM),
		COMMAND,
		INV_CLICK,
		SIGN;
		private ArgType[] argTypes;
		private OpenType(ArgType... supportedArgTypes) {
			this.argTypes = supportedArgTypes;
		}
		public boolean supports(ArgType t) {
			for (ArgType at : argTypes)
				if (at==t) return true;
			return false;
		}
	}
	public static enum ClickType {ATTACK, USE}
	public static enum ArgType {CUSTOM_NAME, REAL_NAME, TAG, NBT, POS, TYPE, UUID, TEAM}

	private final OpenType openType;
	private final ClickType clickType;
	private final ArgType argType;
	private final Object arg;

	protected OpenChecker(OpenType openType, ClickType clickType, ArgType argType, Object arg) {
		this.openType = openType;
		this.clickType = clickType;
		this.argType = argType;
		this.arg = arg;
	}

	public boolean check(OpenType openType, ClickType clickType, Object argument) {
		if (this.openType!=openType || this.clickType!=clickType) {
			return false;
		}

		switch (openType) {
		case BLOCK:
			return argument instanceof Block && checkBlock((Block) argument);
		case BLOCK_ENTITY:
			return argument instanceof BlockEntity && checkBlockEntity((BlockEntity) argument);
		case ITEM:
			return argument instanceof ItemStack && checkItem((ItemStack) argument);
		case ENTITY:
			return argument instanceof Entity && checkEntity((Entity) argument);
		case SIGN:
		case COMMAND:
		case INV_CLICK:
			return (boolean) arg;
		default:
			return false;
		}
	}

	private boolean checkItem(ItemStack argument) {
		switch (argType) {
		case CUSTOM_NAME:
			return arg.equals(argument.getName());
		case REAL_NAME:
			return arg.equals(argument.getItem().getName());
		case TAG:
			return ItemTags.getTagGroup().getTagsFor(argument.getItem()).stream().anyMatch(t->t.equals(arg));
		case NBT:
			return argument.getOrCreateNbt().contains((String) arg);
		default:
			return false;
		}
	}
	private boolean checkEntity(Entity argument) {
		switch (argType) {
		case CUSTOM_NAME:
			return arg.equals(argument.getCustomName());
		case NBT:
			return argument.writeNbt(new NbtCompound()).contains((String) arg);
		case POS:
			return arg.equals(argument.getBlockPos());
		case REAL_NAME:
			return arg.equals(argument.getEntityName());
		case TAG:
			return EntityTypeTags.getTagGroup().getTagsFor(argument.getType()).stream().anyMatch(t->t.equals(arg));
		case TEAM:
			return arg.equals(argument.getScoreboardTeam().getName());
		case TYPE:
			return arg.equals(Registry.ENTITY_TYPE.getId(argument.getType()));
		case UUID:
			return arg.equals(argument.getUuid());
		default:
			return false;
		}
	}

	private boolean checkBlock(Block argument) {
		switch (argType) {
		case REAL_NAME:
			return arg.equals(argument.getName());
		case TAG:
			return BlockTags.getTagGroup().getTagsFor(argument).stream().anyMatch(t->t.equals(arg));
		default:
			return false;
		}
	}

	private boolean checkBlockEntity(BlockEntity argument) {
		switch(argType) {
		case NBT:
			return argument.writeNbt(new NbtCompound()).contains((String) arg);
		case POS:
			return arg.equals(argument.getPos());
		case TYPE:
			return arg.equals(Registry.BLOCK_ENTITY_TYPE.getId(argument.getType()));
		default:
			return false;
		}
	}
	
	public OpenType getOpenType() {
		return openType;
	}

	@Nullable
	public ClickType getClickType() {
		return clickType;
	}

	@Nullable
	public ArgType getArgType() {
		return argType;
	}

	@NotNull
	public Object getArg() {
		return arg;
	}
}