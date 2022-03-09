package megaminds.actioninventory.actions;

import static megaminds.actioninventory.misc.Enums.COMPLETE;

import java.util.Arrays;
import java.util.UUID;

import eu.pb4.sgui.api.ClickType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.consumables.BasicConsumable;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.ConsumableDataHelper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

@NoArgsConstructor
@Getter
@Setter
@PolyName("Consume")
public final class ConsumeAction extends GroupAction {
	private static final BasicConsumable[] EMPTY_C = new BasicConsumable[0];

	/**Consumables to consume*/
	private BasicConsumable[] consumables;
	/**True->pay first time, false->pay every time*/
	private TriState singlePay;
	/**True->Full amount is needed to consume any, false->will consume as much as possible*/
	private TriState requireFull;

	public ConsumeAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, BasicAction[] actions, BasicConsumable[] consumables, TriState singlePay, TriState requireFull) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName, actions);
		this.consumables = consumables;
		this.singlePay = singlePay;
		this.requireFull = requireFull;
	}

	public ConsumeAction(BasicAction[] actions, BasicConsumable[] consumables, TriState singlePay, TriState requireFull) {
		super(actions);
		this.consumables = consumables;
		this.singlePay = singlePay;
		this.requireFull = requireFull;
	}

	@Override
	public void accept(ActionInventoryGui gui) {
		if (consumables==EMPTY_C) {
			super.accept(gui);
			return;
		}

		var p = gui.getPlayer();
		var player = p.getUuid();
		var server = p.getServer();
		var guiName = gui.getId().toString();
		var lastAction = gui.getLastAction();

		var actionData = ConsumableDataHelper.getAction(server, player, guiName, lastAction);

		boolean hasPaidFull = singlePay.orElse(false) && actionData.isPresent() && actionData.orElseThrow().getBoolean(COMPLETE);
		if (!hasPaidFull) {
			boolean canPay = true;
			if (requireFull.orElse(false)) canPay = canPay(server, player, guiName, lastAction);
			if (canPay) {
				pay(server, player, guiName, lastAction);
				hasPaidFull = requireFull.orElse(false);
			}
		}

		if (hasPaidFull) {
			super.accept(gui);
		}
	}

	private void pay(MinecraftServer server, UUID player, String guiName, String lastAction) {
		for (var c : consumables) {
			c.consume(server, player, ConsumableDataHelper.getOrCreateConsumable(server, player, guiName, lastAction, c.getStorageName()));
		}
		if (singlePay.orElse(false)) ConsumableDataHelper.getOrCreateAction(server, player, guiName, lastAction).putBoolean(COMPLETE, true);
	}

	private boolean canPay(MinecraftServer server, UUID player, String guiName, String lastAction) {
		for (var c : consumables) {
			if (!c.canConsumeFull(server, player, ConsumableDataHelper.getConsumable(server, player, guiName, lastAction, c.getStorageName()).orElse(null))) {
				return false;
			}
		}
		return true;
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