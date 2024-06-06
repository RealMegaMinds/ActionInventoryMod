package megaminds.actioninventory.gui.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.gui.BetterGuiI;
import megaminds.actioninventory.gui.PagedGui;
import megaminds.actioninventory.util.ElementHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;

public class ListInput<E> extends InputCallback<List<E>> {
	private ItemStack creatorDisplay;
	private InputCallback<E> creator;
	private Function<E, ItemStack> toStackFunc;
	private int max;

	public ListInput(InputCallback<E> creator) {
		creatorDisplay(null);
		creator(creator);
		toStackFunc(null);
		max(-1);
	}

	@Override
	public InputCallback<List<E>> copy() {
		return new ListInput<>(creator)
				.creatorDisplay(creatorDisplay)
				.toStackFunc(toStackFunc)
				.max(max)
				.question(question)
				.defaultAnswer(defaultAnswer)
				.onFinish(onFinish);
	}

	public ListInput<E> max(int max) {
		this.max = max;
		return this;
	}

	@Override
	public InputCallback<List<E>> defaultAnswer(List<E> defaultAnswer) {
		this.defaultAnswer = Objects.requireNonNullElseGet(defaultAnswer, Collections::emptyList);
		return this;
	}

	public ListInput<E> creator(InputCallback<E> creator) {
		this.creator = Objects.requireNonNull(creator, "Creator cannot be null");
		return this;
	}

	public ListInput<E> creatorDisplay(ItemStack creatorDisplay) {
		this.creatorDisplay = Objects.requireNonNullElseGet(creatorDisplay, ()->{
			var stack = Items.CRAFTING_TABLE.getDefaultStack();
			stack.setCustomName(new LiteralText("Create New"));
			return stack;
		});
		return this;
	}

	public ListInput<E> toStackFunc(Function<E, ItemStack> toStackFunc) {
		this.toStackFunc = Objects.requireNonNullElseGet(toStackFunc, ()->e->Items.WHITE_WOOL.getDefaultStack());
		return this;
	}

	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		final List<E> chosen = new ArrayList<>(Math.max(max, 10));
		chosen.addAll(defaultAnswer);

		var builder = PagedGui.builder(2, false)
				.chained()
				.standardPageElements()
				.standardInputElements((i,t,a,g)->onFinish.onFinish(index, type, action, gui, chosen))
				.controlSkip(4)
				.controlElement(new GuiElement(creatorDisplay, creator.onFinish((i,t,a,g,e)->{
					chosen.add(e);
					updateGui(g, toStackFunc, chosen);
				})));

		var g = builder.build(gui.getPlayer());
		updateGui(g, toStackFunc, chosen);
		g.open(gui);
		return false;
	}

	private void updateGui(SlotGuiInterface gui, Function<E, ItemStack> toStackFunc, List<E> chosen) {
		int cs = chosen.size();
		int gs = gui.getSize();
		int i = 0;
		for (; i<cs; i++) gui.setSlot(i, ElementHelper.getEditable(chosen.get(i), toStackFunc, e->{
			chosen.remove(e);
			updateGui(gui, toStackFunc, chosen);
		}, (old, nw)->{
			chosen.set(chosen.indexOf(old), nw);
			updateGui(gui, toStackFunc, chosen);
		}, creator));
		for (; i<gs; i++) gui.clearSlot(i);

		if (max>0) {
			gui.setTitle(question.shallowCopy().append(" "+cs+"/"+max+" Created"));
		} else {
			gui.setTitle(question.shallowCopy().append(" "+cs+" Created"));
		}
	}
}