package megaminds.actioninventory.gui.input;

import java.util.Objects;
import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.BetterAnvilGui;
import megaminds.actioninventory.gui.BetterGuiI;
import net.minecraft.screen.slot.SlotActionType;

public class StringInput extends FilteredInput<String> {
	@Override
	public InputCallback<String> copy() {
		return new StringInput()
				.filter(filter)
				.postFilter(postFilter)
				.question(question)
				.defaultAnswer(defaultAnswer)
				.onFinish(onFinish);
	}

	@Override
	public InputCallback<String> defaultAnswer(String defaultAnswer) {
		this.defaultAnswer = Objects.requireNonNullElse(defaultAnswer, "");
		return this;
	}

	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		BetterAnvilGui input = new BetterAnvilGui(gui.getPlayer(), false);
		input.setDefaultInputValue(defaultAnswer);
		input.setTitle(question);
		input.setChained(true);
		input.setFilter(filter);
		input.setPostFilter(postFilter);
		input.setOnFinish(s->onFinish.onFinish(index, type, action, gui, s));

		input.open(gui);
		return false;
	}
}