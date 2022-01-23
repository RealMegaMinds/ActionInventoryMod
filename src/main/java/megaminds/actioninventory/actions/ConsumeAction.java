package megaminds.actioninventory.actions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import eu.pb4.sgui.api.ClickType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.consumables.BasicConsumable;
import megaminds.actioninventory.gui.NamedSlotGuiInterface;
import megaminds.actioninventory.misc.StoredConsumables;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@NoArgsConstructor
@Getter
@Setter
@PolyName("Consume")
public final class ConsumeAction extends GroupAction {
	private static final Map<UUID, StoredConsumables> STORED_CONSUMABLES = new HashMap<>();
	private static final BasicConsumable[] EMPTY_C = new BasicConsumable[0];
	
	/**Consumables to consume*/
	private BasicConsumable[] consumables;
	/**True->pay first time, false->pay every time*/
	private boolean singlePay;
	/**True->Full amount is needed to consume any, false->will consume as much as possible*/
	private boolean requireFull;
	
	public ConsumeAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Boolean requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, BasicAction[] actions, BasicConsumable[] consumables, boolean singlePay, boolean requireFull) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName, actions);
		this.consumables = consumables;
		this.singlePay = singlePay;
		this.requireFull = requireFull;
	}
	
	public ConsumeAction(BasicAction[] actions, BasicConsumable[] consumables, boolean singlePay, boolean requireFull) {
		super(actions);
		this.consumables = consumables;
		this.singlePay = singlePay;
		this.requireFull = requireFull;
	}

	@Override
	public void execute(NamedSlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		StoredConsumables store = getStore(player.getUuid());
		String guiName = gui.getName().toString();
		String lastAction = gui.lastAction();
		NbtElement topElement = store.getSub(guiName, lastAction);

		//NbtByte.ONE means the full price has been paid
		boolean hasPaid = singlePay && NbtByte.ONE.equals(topElement);
		if (!hasPaid) {
			NbtCompound subStore = ((NbtCompound)topElement);
			boolean canPay = checkConsumption(player, subStore);
			
			if (canPay || !requireFull) {
				if (!canPay&&subStore==null) {
					subStore = new NbtCompound();
					store.setSub(guiName, lastAction, subStore);
				}
				consume(player, subStore, !canPay);
				store.save();
				hasPaid = canPay;
			}
		}
		
		if (hasPaid) {
			super.execute(gui);
			if (singlePay) {
				store.setSub(guiName, lastAction, NbtByte.ONE);
			} else {
				store.removeSub(guiName, lastAction);
			}
		}		
	}
	
	/**
	 * Checks if the player can consume the full amount from all consumables
	 */
	private boolean checkConsumption(ServerPlayerEntity p, NbtCompound subStore) {
		for (BasicConsumable c : consumables) {
			NbtElement current = null;
			if (subStore!=null) current = subStore.get(c.getStorageName());
			if (current!=null) current = current.copy();	//current is copied because it should not be altered in this method
			if (!c.canConsumeFull(p, current)) return false;
		}
		return true;
	}
	
	/**
	 * Actually does consuming
	 */
	private void consume(ServerPlayerEntity p, NbtCompound subStore, boolean saveAmount) {
		for (BasicConsumable c : consumables) {
			String cName = c.getStorageName();
			NbtElement og = null;
			if (subStore!=null) og = subStore.get(cName);
			NbtElement result = c.consume(p, og, saveAmount);
			if (saveAmount) subStore.put(cName, result);	//NOSONAR Not null here
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
	
	@Override
	public BasicAction copy() {
		return new ConsumeAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), Arrays.stream(getActions()).map(BasicAction::copy).toArray(BasicAction[]::new), Arrays.stream(consumables).map(BasicConsumable::copy).toArray(BasicConsumable[]::new), singlePay, requireFull);
	}
}