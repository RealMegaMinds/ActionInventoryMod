package megaminds.testmod.inventory;

import static megaminds.testmod.inventory.OpenChecker.ClickType.USE;
import static megaminds.testmod.inventory.OpenChecker.OpenType.COMMAND;
import static megaminds.testmod.inventory.OpenChecker.OpenType.INV_CLICK;
import static megaminds.testmod.inventory.OpenChecker.OpenType.ITEM;
import static megaminds.testmod.inventory.OpenChecker.OpenType.SIGN;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import megaminds.testmod.inventory.OpenChecker.ArgType;
import megaminds.testmod.inventory.OpenChecker.ClickType;
import megaminds.testmod.inventory.OpenChecker.OpenType;
import net.minecraft.tag.Tag;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class OpenCheckerDeserializer implements JsonDeserializer<OpenChecker> {
	private static final String OPEN_TYPE = "openType", CLICK_TYPE = "clickType", ARG_TYPE = "argType", ARG = "arg";

	@Override
	public OpenChecker deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		if (!jsonObject.has(OPEN_TYPE) || !jsonObject.has(ARG)) {
			throw new JsonParseException("An open requirement needs at least an open type and an argument");
		}
		OpenType openT = context.deserialize(jsonObject.get(OPEN_TYPE), OpenType.class);
		JsonElement arg = jsonObject.get(ARG);

		if (openT==COMMAND||openT==INV_CLICK) {
			return new OpenChecker(openT, null, null, arg.getAsString());
		}

		if (!jsonObject.has(CLICK_TYPE)) {
			throw new JsonParseException("OpenType: "+openT+" requires a ClickType");
		}

		ClickType clickT = context.deserialize(jsonObject.get(CLICK_TYPE), ClickType.class);
		if (openT==ITEM && clickT!=USE) {
			throw new JsonParseException("OpenType: "+openT+" doesn't support ClickType: "+clickT);
		}

		if (openT==SIGN) {
			return new OpenChecker(openT, clickT, null, arg);
		}

		if (!jsonObject.has(ARG_TYPE)) {
			throw new JsonParseException("OpenType: "+openT+" requires an ArgType");
		}

		ArgType argT = context.deserialize(jsonObject.get(ARG_TYPE), ArgType.class);
		if (!openT.supports(argT)) {
			throw new JsonParseException("OpenType: "+openT+" doesn't support ArgType: "+argT);
		}

		switch (argT) {
		case CUSTOM_NAME:
		case REAL_NAME:
			return new OpenChecker(openT, clickT, argT, context.deserialize(arg, MutableText.class));
		case UUID:
			return new OpenChecker(openT, clickT, argT, context.deserialize(arg, UUID.class));
		case NBT:
			return new OpenChecker(openT, clickT, argT, arg.getAsString());
		case POS:
			return new OpenChecker(openT, clickT, argT, context.deserialize(arg, BlockPos.class));
		case TAG:
			return new OpenChecker(openT, clickT, argT, context.deserialize(arg, Tag.class));
		case TEAM:
			return new OpenChecker(openT, clickT, argT, arg.getAsString());
		case TYPE:
			return new OpenChecker(openT, clickT, argT, context.deserialize(arg, Identifier.class));
		default:
			throw new JsonParseException("Internal Error Deserializing OpenChecker");
		}
	}
}