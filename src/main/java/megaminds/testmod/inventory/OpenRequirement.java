package megaminds.testmod.inventory;

import java.util.Arrays;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;

import static megaminds.testmod.inventory.OpenRequirement.OpenType.*;
import static megaminds.testmod.inventory.OpenRequirement.ClickType.*;
import static megaminds.testmod.inventory.OpenRequirement.ArgType.*;

public class OpenRequirement {
	public static enum OpenType {BLOCK, ITEM, ENTITY, COMMAND, INV_CLICK, SIGN;
		public static Optional<OpenType> optionalValueOf(String value){
			try {
				return Optional.of(valueOf(value));
			} catch (IllegalArgumentException e) {
				return Optional.empty();
			}
		}
	}
	public static enum ClickType {ATTACK, USE;
		public static Optional<ClickType> optionalValueOf(String value){
			try {
				return Optional.of(valueOf(value));
			} catch (IllegalArgumentException e) {
				return Optional.empty();
			}
		}
	}

	public static enum ArgType {CUSTOM_NAME, REAL_NAME, TAG, NBT, POS, TYPE, UUID, TEAM;
		public static Optional<ArgType> optionalValueOf(String value){
			try {
				return Optional.of(valueOf(value));
			} catch (IllegalArgumentException e) {
				return Optional.empty();
			}
		}
	}

	private final OpenType openType;
	private final ClickType clickType;
	private final ArgType argType;
	private final String arg;

	private OpenRequirement(OpenType openType, ClickType clickType, ArgType argType, String arg) {
		this.openType = openType;
		this.clickType = clickType;
		this.argType = argType;
		this.arg = arg;
	}

	public static boolean check(OpenRequirement req, OpenType type, ClickType clickType, Object argument) {
		if (req.openType!=type) {
			return false;
		}
		switch (type) {
		case BLOCK:
			return checkBlock(req, clickType, argument);
		case ITEM:
			return checkItem(req, clickType, argument);
		case ENTITY:
			return checkEntity(req, clickType, argument);
		case SIGN:
			return clickType.equals(clickType) && "true".equals(req.arg);
		case COMMAND:
		case INV_CLICK:
			return "true".equals(req.arg);
		}

		return false;
	}

	public static OpenRequirement createOpenRequirement(String openType, String clickType, String argType, String arg) {
		if (arg==null||arg.isBlank()) {
			System.err.println("Arg cannot be empty");
			return null;
		}

		Optional<OpenType> openO = OpenType.optionalValueOf(enumFormat(openType));
		if (openO.isEmpty()) {
			System.err.println("openType must match an OpenType");
			return null;
		}

		OpenType openR = openO.get();
		if (openR==COMMAND||openR==INV_CLICK) {
			return new OpenRequirement(openR, null, null, arg);
		}

		Optional<ClickType> clickT = ClickType.optionalValueOf(enumFormat(clickType));
		if (clickT.isEmpty()) {
			System.err.println("OpenType: "+openO+" requires a ClickType");
			return null;
		}

		ClickType clickR = clickT.get();
		if (openR==ITEM && clickR!=USE) {
			System.err.println("OpenType: "+ITEM+" doesn't support ClickType: "+clickR);
			return null;
		}

		if (openR==SIGN) {
			return new OpenRequirement(openR, clickR, null, arg);
		}

		Optional<ArgType> argT = ArgType.optionalValueOf(enumFormat(argType));
		if (argT.isEmpty()) {
			System.err.println("OpenType: "+openO+" requires an ArgType");
		}

		ArgType argR = argT.get();
		boolean allowed = false;
		switch (openR) {
		case BLOCK:
			allowed = isOneOf(argR, REAL_NAME, TAG, NBT, POS, TYPE);
			break;
		case ENTITY:
			allowed = isOneOf(argR, CUSTOM_NAME, REAL_NAME, TAG, NBT, POS, TYPE, TEAM, UUID);
			break;
		case ITEM:
			allowed = isOneOf(argR, CUSTOM_NAME, REAL_NAME, TAG, NBT);
			break;
		default:
			break;
		}
		if (!allowed) {
			System.err.println("OpenType: "+ITEM+" doesn't support ArgType: "+argR);
			return null;
		}
		return new OpenRequirement(openR, clickR, argR, arg);
	}

	@SafeVarargs
	private static <T extends Enum<T>> boolean isOneOf(T obj, T... arr) {
		for(T t : arr)if(t==obj)return true;return false;
	}

	private static String enumFormat(String s) {
		if (s==null) return "";
		return s.toUpperCase().strip().replace(' ', '_');
	}

	private static boolean checkItem(OpenRequirement req, ClickType type, Object argument) {
		if (argument instanceof ItemStack) {
			//SUPPORTS: CUSTOM_NAME, REAL_NAME, TAG, NBT
			//TODO finish
			return false;
		} else {
			argumentError(req.openType, ItemStack.class);
			return false;
		}
	}
	private static boolean checkEntity(OpenRequirement req, ClickType type, Object argument) {
		//SUPPORTS: CUSTOM_NAME, REAL_NAME, TAG, NBT, POS, TYPE, TEAM, UUID
		if (argument instanceof ItemStack) {
			//TODO finish
			return false;
		} else {
			argumentError(req.openType, Entity.class);
			return false;
		}
	}
	private static boolean checkBlock(OpenRequirement req, ClickType type, Object argument) {
		switch (req.argType) {
		case REAL_NAME:
		case TAG:
			Block b;
			if (argument instanceof Block) {
				b = (Block) argument;
			} else if (argument instanceof BlockState) {
				b = ((BlockState)argument).getBlock();
			} else if (argument instanceof BlockEntity) {
				b = ((BlockEntity)argument).getCachedState().getBlock();
			} else {
				argumentError(req.openType, Block.class, BlockState.class, BlockEntity.class);
				return false;
			}
			String arg = req.argType==REAL_NAME ? b.getName().asString() : null;
			return req.arg.equalsIgnoreCase(arg);

			//TODO finish cases
		case NBT:
		case POS:
		case TYPE:
		default:
			break;
		}
		return false;
	}

	private static void argumentError(OpenType type, Class<?>... arr) {
		System.err.println("The argument for "+type+" should be one of "+Arrays.stream(arr).map(c->c.getName()).distinct().toArray(String[]::new));
	}

	public OpenType getOpenType() {
		return openType;
	}

	public ClickType getClickType() {
		return clickType;
	}

	public ArgType getArgType() {
		return argType;
	}

	public String getArg() {
		return arg;
	}
}