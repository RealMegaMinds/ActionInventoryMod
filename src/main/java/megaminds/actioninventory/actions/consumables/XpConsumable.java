package megaminds.actioninventory.actions.consumables;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.actions.Action;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.ConsumeAction;
import megaminds.actioninventory.gui.NamedGui;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

public class XpConsumable extends BasicConsumable {
	private static enum Kind{XP, LEVEL};
	
	private static final String KIND = "kind", AMOUNT = "amount";
	
	private Kind kind;
	private int amount;

	@Override
	protected boolean shouldExecuteActions(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		NbtCompound nbt = getStore(player.getUuid()).getOrCreateSub(((NamedGui)gui).getName(), index);
		return (nbt.contains("xp")?nbt.getInt("xp"):0)+(kind==Kind.XP?player.totalExperience:player.experienceLevel) >= amount;
	}
	
	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		kind = obj.has(KIND) ? context.deserialize(obj.get(KIND), Kind.class) : Kind.LEVEL;
		amount = obj.has(AMOUNT) ? obj.get(AMOUNT).getAsInt() : 0;
		return super.fromJson(obj, context);
	}

	@Override
	protected void consume(int index, ClickType type, SlotActionType action, SlotGuiInterface gui, boolean logToStore) {
		ServerPlayerEntity player = gui.getPlayer();
		NbtCompound nbt = getStore(player.getUuid()).getOrCreateSub(((NamedGui)gui).getName(), index);
		int toPay = nbt.contains("xp") ? amount-nbt.getInt("xp") : amount;
		if (kind==Kind.LEVEL) {
			player.addExperienceLevels(-toPay);
		} else {
			player.addExperience(-toPay);
		}
		if (logToStore) {			
			nbt.putInt("xp", amount);
		}
	}

	@Override
	public Action getType() {
		return Action.REQUIRE_XP;
	}
}