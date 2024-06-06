package megaminds.actioninventory.gui.input;

import java.util.Objects;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.BetterGui;
import megaminds.actioninventory.gui.BetterGuiI;
import megaminds.actioninventory.util.ElementHelper;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;

public class BooleanInput extends InputCallback<Boolean> {
	private String trueName;
	private String falseName;

	public BooleanInput() {
		trueName = "True";
		falseName = "False";
	}

	@Override
	public InputCallback<Boolean> copy() {
		return new BooleanInput()
				.trueName(trueName)
				.falseName(falseName)
				.question(question)
				.defaultAnswer(defaultAnswer)
				.onFinish(onFinish);
	}

	public BooleanInput trueName(String trueName) {
		this.trueName = Objects.requireNonNullElse(trueName, "True");
		return this;
	}

	public BooleanInput falseName(String falseName) {
		this.falseName = Objects.requireNonNullElse(falseName, "False");
		return this;
	}

	@Override
	public InputCallback<Boolean> defaultAnswer(Boolean defaultAnswer) {
		this.defaultAnswer = defaultAnswer;
		return this;
	}

	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		BetterGui bg = new BetterGui(ScreenHandlerType.GENERIC_9X1, gui.getPlayer(), false);
		bg.setChained(true);
		bg.setTitle(question);

		bg.setSlot(0, ElementHelper.getCancel());
		bg.setSlot(3, ElementHelper.of(Items.WATER_BUCKET, trueName, 1, defaultAnswer==Boolean.TRUE, true, (i,t,a,g)->onFinish.onFinish(index, type, action, gui, Boolean.TRUE)));
		bg.setSlot(5, ElementHelper.of(Items.BUCKET, falseName, 1, defaultAnswer==Boolean.FALSE, true, (i,t,a,g)->onFinish.onFinish(index, type, action, gui, Boolean.FALSE)));
		bg.open(gui);
		return false;
	}
}