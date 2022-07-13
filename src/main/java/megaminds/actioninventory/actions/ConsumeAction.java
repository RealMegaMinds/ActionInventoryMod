package megaminds.actioninventory.actions;

import static megaminds.actioninventory.misc.Enums.COMPLETE;

import java.util.Arrays;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.consumables.BasicConsumable;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.ConsumableDataHelper;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

@NoArgsConstructor
@Getter
@Setter
@PolyName("Consume")
public final class ConsumeAction extends GroupAction {
	/**Consumables to consume*/
	private BasicConsumable[] consumables;
	/**True->pay first time, false->pay every time*/
	private TriState singlePay = TriState.DEFAULT;
	/**True->Full amount is needed to consume any, false->will consume as much as possible*/
	private TriState requireFull = TriState.DEFAULT;

	public ConsumeAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, ClickAwareAction[] actions, BasicConsumable[] consumables, TriState singlePay, TriState requireFull) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName, actions);
		this.consumables = consumables;
		this.singlePay = singlePay;
		this.requireFull = requireFull;
	}

	public ConsumeAction(ClickAwareAction[] actions, BasicConsumable[] consumables, TriState singlePay, TriState requireFull) {
		super(actions);
		this.consumables = consumables;
		this.singlePay = singlePay;
		this.requireFull = requireFull;
	}

	@Override
	public void accept(ActionInventoryGui gui) {
		if (consumables==null) {
			super.accept(gui);
			return;
		}

		var p = gui.getPlayer();
		var player = p.getUuid();
		var server = p.getServer();
		var guiName = gui.getId().toString();
		var lastAction = gui.getLastAction();
		var singlePayB = singlePay.orElse(false);
		var requireFullB = requireFull.orElse(false);

		var actionData = ConsumableDataHelper.getAction(server, player, guiName, lastAction);

		var hasPaid = singlePayB && Helper.getBoolean(actionData.orElse(null), COMPLETE);	//can be complete and is complete

		//the full amount has not been paid and [the full amount is not required or (the full amount is required and can be paid)].
		if (!hasPaid && (!requireFullB || canPayFull(server, player, guiName, lastAction))) hasPaid = pay(server, player, guiName, lastAction);

		//the full amount was paid or has now been paid
		if (hasPaid) {
			if (singlePayB) {
				forgetData(ConsumableDataHelper.getOrCreateAction(server, player, guiName, lastAction), true);
			} else {
				ConsumableDataHelper.getAction(server, player, guiName, lastAction).ifPresent(c->forgetData(c, false));
			}
			super.accept(gui);
		}
	}

	/**
	 * Replaces consumable data with complete.
	 */
	private void forgetData(@NotNull NbtCompound compound, boolean markComplete) {
		compound.getKeys().clear();
		if (markComplete) compound.putBoolean(COMPLETE, true);
	}

	/**
	 * Makes each consumable consume as much as possible up to the full amount.
	 */
	private boolean pay(MinecraftServer server, UUID player, String guiName, String lastAction) {
		if (consumables==null) return true;

		var paidFull = true;
		for (var c : consumables) {
			if (!c.consume(server, player, ConsumableDataHelper.getOrCreateConsumable(server, player, guiName, lastAction, c.getStorageName()))) paidFull = false;
		}
		return paidFull;
	}

	/**
	 * Returns true if all consumables can pay the full amount.
	 */
	private boolean canPayFull(MinecraftServer server, UUID player, String guiName, String lastAction) {
		if (consumables==null) return true;

		for (var c : consumables) {
			if (!c.canConsumeFull(server, player, ConsumableDataHelper.getConsumable(server, player, guiName, lastAction, c.getStorageName()).orElse(null))) {
				return false;
			}
		}
		return true;
	}

	@Override
<<<<<<< Updated upstream
	public BasicAction copy() {
		return new ConsumeAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), Arrays.stream(getActions()).map(BasicAction::copy).toArray(BasicAction[]::new), Arrays.stream(consumables).map(BasicConsumable::copy).toArray(BasicConsumable[]::new), singlePay, requireFull);
=======
	public void validate() {
		super.validate();
		if (consumables==null) consumables = EMPTY_C;
	}

	@Override
	public ClickAwareAction copy() {
		return new ConsumeAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), Arrays.stream(getActions()).map(ClickAwareAction::copy).toArray(ClickAwareAction[]::new), Arrays.stream(consumables).map(BasicConsumable::copy).toArray(BasicConsumable[]::new), singlePay, requireFull);
>>>>>>> Stashed changes
	}
}