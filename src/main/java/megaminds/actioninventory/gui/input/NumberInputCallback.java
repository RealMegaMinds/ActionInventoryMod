package megaminds.actioninventory.gui.input;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.BetterAnvilGui;
import megaminds.actioninventory.gui.BetterGuiI;
import net.minecraft.screen.slot.SlotActionType;

public abstract class NumberInputCallback<T extends Number> extends FilteredInput<T> {
	protected abstract T strToNum(String str);

	public abstract NumberInputCallback<T> boundedPostFilter(T min, T max);

	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		BetterAnvilGui input = new BetterAnvilGui(gui.getPlayer(), false);
		input.setDefaultInputValue(defaultAnswer+"");
		input.setTitle(question);
		input.setChained(true);
		input.setFilter(s->filter.apply(strToNum(s)).toString());
		input.setPostFilter(s->postFilter.apply(strToNum(s)).toString());
		input.setOnFinish(s->onFinish.onFinish(index, type, action, gui, strToNum(s)));

		input.open(gui);
		return false;
	}
}