package megaminds.actioninventory.actions;

import static megaminds.actioninventory.util.JsonHelper.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.consumables.BasicConsumable;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.StoredConsumables;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

public class ConsumeAction extends BasicAction {
	private static final Map<UUID, StoredConsumables> STORED_CONSUMABLES = new HashMap<>();
	private static final String CONSUMABLES = "consumables", SINGLE_PAY = "singlePay", REQUIRE_FULL = "requireFullAmountForAll", ACTIONS = "actions";
	
	/**Consumables to consume*/
	private BasicConsumable[] consumables;
	/**True->pay first time, false->pay every time*/
	private boolean singlePay;
	/**True->Full amount is needed to consume any, false->will consume as much as possible*/
	private boolean requireFull;
	/**Actions to execute when full amount is consumed*/
	private BasicAction[] actions;
	
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity p = gui.getPlayer();
		StoredConsumables s = getStore(p.getUuid());
		NbtElement sc = s.getOrCreateSub(gui.getName(), index, NbtCompound::new);
		
		if (singlePay && sc.equals(NbtByte.ONE)) {
			execute(index, type, action, gui);
			return;
		}
		
		Function<String, NbtElement> getSub = str->s.getDeepSub(gui.getName(), index, str);
		
		if (requireFull && !checkConsumption(p, getSub)) {
			return;
		}
		
		consume(p, getSub, (str,e)->s.setDeepSub(gui.getName(), index, str, e));
		if (singlePay) s.setSub(gui.getName(), index, NbtByte.ONE);
		execute(index, type, action, gui);
	}
	
	/**
	 * Checks if the player can consume the full amount from all consumables
	 */
	private boolean checkConsumption(ServerPlayerEntity p, Function<String, NbtElement> func) {
		for (BasicConsumable c : consumables) {
			if (!c.canConsumeFull(p, func.apply(c.getType().toString()))) return false;
		}
		return true;
	}
	
	/**
	 * Actually does consuming
	 */
	private void consume(ServerPlayerEntity p, Function<String, NbtElement> func, BiConsumer<String, NbtElement> resultConsumer) {
		for (BasicConsumable c : consumables) {
			String type = c.getType().toString();
			resultConsumer.accept(type, c.consume(p, func.apply(type)));
		}
	}

	/**
	 * Executes all actions.
	 */
	private void execute(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		for (BasicAction a : actions) {
			a.internalClick(index, type, action, gui);
		}
	}
	
	private StoredConsumables getStore(UUID uuid) {
		return STORED_CONSUMABLES.computeIfAbsent(uuid, StoredConsumables::new);
	}
	
	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		actions = clazzArr(obj.get(ACTIONS), BasicAction.class, context);
		consumables = clazzArr(obj.get(CONSUMABLES), BasicConsumable.class, context);
		singlePay = bool(obj.get(SINGLE_PAY));
		requireFull = bool(obj.get(REQUIRE_FULL), true);
		if (requireFull) Helper.forEach(consumables, BasicConsumable::requireFull);
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		obj.add(ACTIONS, context.serialize(actions));
		obj.add(CONSUMABLES, context.serialize(consumables));
		obj.addProperty(REQUIRE_FULL, requireFull);
		obj.addProperty(SINGLE_PAY, singlePay);
		return obj;
	}

	@Override
	public Action getType() {
		return Action.CONSUME;
	}
}