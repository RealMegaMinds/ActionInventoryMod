package megaminds.actioninventory.gui.input;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import megaminds.actioninventory.gui.BetterGuiI;
import megaminds.actioninventory.gui.PagedGui;
import megaminds.actioninventory.gui.callback.BetterClickCallback;
import megaminds.actioninventory.util.ElementHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class InputSplitter implements BetterClickCallback {
	private Set<ClickCallback> choices;
	private Function<ClickCallback, ItemStack> toStackFunc;
	private Text question;

	public InputSplitter() {
		choices((Map<ClickCallback, ItemStack>)null);
		question(null);
	}

	public InputSplitter question(Text question) {
		this.question = Objects.requireNonNullElse(question, LiteralText.EMPTY);
		return this;
	}

	public InputSplitter toStackFunc(Function<ClickCallback, ItemStack> toStackFunc) {
		this.toStackFunc = Objects.requireNonNullElseGet(toStackFunc, ()->c->Items.WHITE_WOOL.getDefaultStack());
		return this;
	}

	public InputSplitter choices(Map<ClickCallback, ItemStack> choices) {
		if (choices!=null) {
			this.choices = choices.keySet();
			toStackFunc(choices::get);
		} else {
			this.choices = Collections.emptySet();
			toStackFunc(null);
		}
		return this;
	}

	public InputSplitter choices(Set<ClickCallback> choices) {
		this.choices = Objects.requireNonNullElseGet(choices, Collections::emptySet);
		return this;
	}

	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		int rows = PagedGui.minRows(choices.size()) + 1;

		var builder = PagedGui.builder(rows, false)
				.chained()
				.standardPageElements()
				.controlSkip(4)
				.controlElement(ElementHelper.getCancel())
				.title(question);

		for (var choice : choices) {
			builder.element(new GuiElement(toStackFunc.apply(choice), (i,t,a,g)->choice.click(index, type, action, gui)));
		}

		builder.build(gui.getPlayer()).open(gui);
		return false;
	}
}