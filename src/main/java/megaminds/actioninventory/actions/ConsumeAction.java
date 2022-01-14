package megaminds.actioninventory.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.consumables.BasicConsumable;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.misc.StoredConsumables;
import megaminds.actioninventory.util.annotations.TypeName;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

@TypeName("Consume")
public final class ConsumeAction extends GroupAction {
	private static final Map<UUID, StoredConsumables> STORED_CONSUMABLES = new HashMap<>();
	private static final BasicConsumable[] EMPTY_C = new BasicConsumable[0];
	
	/**Consumables to consume*/
	private BasicConsumable[] consumables;
	/**True->pay first time, false->pay every time*/
	private boolean singlePay;
	/**True->Full amount is needed to consume any, false->will consume as much as possible*/
	private boolean requireFull;
	
	private ConsumeAction() {}
	
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity p = gui.getPlayer();
		StoredConsumables s = getStore(p.getUuid());
		NbtElement sc = s.getOrCreateSub(gui.getName(), index, NbtCompound::new);
		
		if (singlePay && sc.equals(NbtByte.ONE)) {
			super.internalClick(index, type, action, gui);
			return;
		}
		
		Function<String, NbtElement> getSub = str->s.getDeepSub(gui.getName(), index, str);
		
		if (requireFull && !checkConsumption(p, getSub)) {
			return;
		}
		
		consume(p, getSub, (str,e)->s.setDeepSub(gui.getName(), index, str, e));
		if (singlePay) s.setSub(gui.getName(), index, NbtByte.ONE);
		super.internalClick(index, type, action, gui);
	}
	
	/**
	 * Checks if the player can consume the full amount from all consumables
	 */
	private boolean checkConsumption(ServerPlayerEntity p, Function<String, NbtElement> func) {
		for (BasicConsumable c : consumables) {
			if (!c.canConsumeFull(p, func.apply(c.getStorageName()))) return false;
		}
		return true;
	}
	
	/**
	 * Actually does consuming
	 */
	private void consume(ServerPlayerEntity p, Function<String, NbtElement> func, BiConsumer<String, NbtElement> resultConsumer) {
		for (BasicConsumable c : consumables) {
			String type = c.getStorageName();
			resultConsumer.accept(type, c.consume(p, func.apply(type)));
		}
	}

	private StoredConsumables getStore(UUID uuid) {
		return STORED_CONSUMABLES.computeIfAbsent(uuid, StoredConsumables::new);
	}

	@Override
	public void validate() {
		super.validate();
		if (consumables==null) consumables = EMPTY_C;
	}
}