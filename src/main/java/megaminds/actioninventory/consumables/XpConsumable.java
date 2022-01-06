package megaminds.actioninventory.consumables;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.JsonHelper;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.server.network.ServerPlayerEntity;

public class XpConsumable extends BasicConsumable {
	private static final String LEVEL = "level", AMOUNT = "amount";

	private int level, amount;

	@Override
	public boolean canConsumeFull(ServerPlayerEntity player, NbtElement storage) {
		if (NbtByte.ONE.equals(storage)) return true;
		int[] saved = {player.experienceLevel, player.totalExperience};
		Helper.getDo(storage, NbtCompound.class::cast, c->{
			Helper.getDo(c.get(LEVEL), NbtInt.class::cast, n->saved[0]+=n.intValue());
			Helper.getDo(c.get(AMOUNT), NbtInt.class::cast, n->saved[1]+=n.intValue());
		});
		return (saved[0] >= level) && (saved[1] >= amount);
	}

	@Override
	public NbtElement consume(ServerPlayerEntity player, NbtElement storage) {
		if (NbtByte.ONE.equals(storage)) return storage;

		NbtCompound compound = Helper.getOrDefault(storage, NbtCompound.class::cast, NbtCompound::new);
		int[] paid = {
				Helper.getOrDefault(compound.get(LEVEL), NbtInt.class::cast, NbtInt.of(0)).intValue(),
				Helper.getOrDefault(compound.get(AMOUNT), NbtInt.class::cast, NbtInt.of(0)).intValue()
		};

		int[] toPay = {
				Math.min(level-paid[0], player.experienceLevel),
				Math.min(amount-paid[1], player.totalExperience)
		};
		
		player.addExperienceLevels(-toPay[0]);
		player.addExperience(-toPay[1]);
		paid[0] += toPay[0];
		paid[1] += toPay[1];
		
		if (paid[0]>=level && paid[1]>=amount) {
			return NbtByte.ONE;
		}
		compound.putInt(LEVEL, level);
		compound.putInt(AMOUNT, amount);
		return compound;
	}

	@Override
	public BasicConsumable fromJson(JsonObject obj, JsonDeserializationContext context) {
		level = JsonHelper.getOrDefault(obj.get(LEVEL), JsonElement::getAsInt, 0);
		amount = JsonHelper.getOrDefault(obj.get(AMOUNT), JsonElement::getAsInt, 0);
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		obj.addProperty(LEVEL, level);
		obj.addProperty(AMOUNT, amount);
		return obj;
	}

	@Override
	public Consumable getType() {
		return Consumable.XP;
	}
}