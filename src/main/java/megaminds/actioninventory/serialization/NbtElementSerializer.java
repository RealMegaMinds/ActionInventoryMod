package megaminds.actioninventory.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import megaminds.actioninventory.util.JsonHelper;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtNull;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

import static net.fabricmc.fabric.api.util.NbtType.*;

public class NbtElementSerializer implements JsonDeserializer<NbtElement>, JsonSerializer<NbtElement> {
	@Override
	public JsonElement serialize(NbtElement src, Type typeOfSrc, JsonSerializationContext context) {
		if (src==null) return null;
		return switch (src.getType()) {
		case END -> JsonNull.INSTANCE;
		case BYTE -> new JsonPrimitive(((NbtByte)src).byteValue());
		case SHORT -> new JsonPrimitive(((NbtShort)src).shortValue());
		case INT -> new JsonPrimitive(((NbtInt)src).intValue());
		case LONG -> new JsonPrimitive(((NbtLong)src).longValue());
		case FLOAT -> new JsonPrimitive(((NbtFloat)src).floatValue());
		case DOUBLE -> new JsonPrimitive(((NbtDouble)src).doubleValue());
		case BYTE_ARRAY -> context.serialize(((NbtByteArray)src).getByteArray());
		case STRING -> new JsonPrimitive(((NbtString)src).asString());
		case LIST -> {
			JsonArray arr = new JsonArray();
			((NbtList)src).forEach(n->arr.add(serialize(n, NbtElement.class, context)));
			yield arr;
		}
		case COMPOUND -> {
			JsonObject obj = new JsonObject();
			NbtCompound c = (NbtCompound)src;
			Set<String> keys = c.getKeys();
			keys.forEach(k->obj.add(k, serialize(c.get(k), NbtElement.class, context)));
			yield obj;
		}
		case INT_ARRAY -> context.serialize(((NbtIntArray)src).getIntArray());
		case LONG_ARRAY -> context.serialize(((NbtLongArray)src).getLongArray());
		default -> throw new IllegalArgumentException("Cannot serialize NbtElement of type: "+src.getType()+" ("+src.getNbtType().getCommandFeedbackName()+")");
		};
	}

	@Override
	public NbtElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (JsonHelper.isNull(json)) {
			return NbtNull.INSTANCE;
		} else if (json.isJsonArray()) {
			return JsonHelper.customList(json, e->deserialize(e, NbtElement.class, context), false, new NbtList());
		} else if (json.isJsonObject()) {
			NbtCompound c = new NbtCompound();
			json.getAsJsonObject().entrySet().forEach(e->c.put(e.getKey(), deserialize(e.getValue(), NbtElement.class, context)));
			return c;
		}
		
		JsonPrimitive p = json.getAsJsonPrimitive();
		if (p.isBoolean()) {
			return NbtByte.of(p.getAsBoolean());
		} else if (p.isString()) {
			return NbtString.of(p.getAsString());
		}
		
		Number n = json.getAsNumber();
		return MAPPER.get(n.getClass()).apply(n);
	}
	
	private static final Map<Class<? extends Number>, Function<Number, NbtElement>> MAPPER = Collections.unmodifiableMap(Map.of(
			Integer.class, n->NbtInt.of(n.intValue()),
			Byte.class, n->NbtByte.of(n.byteValue()),
			Float.class, n->NbtFloat.of(n.floatValue()),
			Double.class, n->NbtDouble.of(n.doubleValue()),
			Long.class, n->NbtLong.of(n.longValue()),
			Short.class, n->NbtShort.of(n.shortValue()),
			BigInteger.class, n->NbtLong.of(n.longValue()),
			BigDecimal.class, n->NbtDouble.of(n.doubleValue())));
}