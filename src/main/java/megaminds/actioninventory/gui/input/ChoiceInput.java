package megaminds.actioninventory.gui.input;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.gui.BetterGuiI;
import megaminds.actioninventory.gui.PagedGui;
import megaminds.actioninventory.util.ElementHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;

public class ChoiceInput<E> extends InputCallback<Set<E>> {
	protected Set<E> choices;
	protected Function<E, ItemStack> toStackFunc;
	protected int max;

	public ChoiceInput() {
		choices = Collections.emptySet();
		toStackFunc = ChoiceInput::toStack;
		max = 1;
	}

	@Override
	public InputCallback<Set<E>> copy() {
		return new ChoiceInput<E>()
				.choices(choices)
				.toStackFunc(toStackFunc)
				.max(max)
				.question(question)
				.defaultAnswer(choices)
				.onFinish(onFinish);
	}

	public ChoiceInput<E> defaultAnswer(Set<E> defaults) {
		this.defaultAnswer = Objects.requireNonNullElseGet(defaults, Collections::emptySet);
		return this;
	}

	public ChoiceInput<E> choices(E[] choices) {
		return choices(Set.of(choices));
	}

	public ChoiceInput<E> choices(List<E> choices) {
		return choices(Set.copyOf(choices));
	}

	public ChoiceInput<E> choices(Set<E> choices) {
		this.choices = Objects.requireNonNullElseGet(choices, Collections::emptySet);
		return this;
	}

	public ChoiceInput<E> toStackFunc(Function<E, ItemStack> toStackFunc) {
		this.toStackFunc = Objects.requireNonNullElse(toStackFunc, ChoiceInput::toStack);
		return this;
	}

	public ChoiceInput<E> max(int max) {
		this.max = Math.max(max, 1);
		return this;
	}

	private static ItemStack toStack(Object e) {
		return Items.PAPER.getDefaultStack().setCustomName(new LiteralText(e.toString()));
	}

	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		Set<E> chosen = new HashSet<>(max);
		chosen.addAll(defaultAnswer);
		int rows = PagedGui.minRows(choices.size()) + 1;

		var builder = PagedGui.builder(rows, false)
				.chained()
				.standardPageElements()
				.standardInputElements((i,t,a,g2)->onFinish.onFinish(index, type, action, gui, chosen));

		BiFunction<SlotGuiInterface, E, Boolean> select = (sg,x)->{
			if (max<0 || chosen.size()<max) {
				chosen.add(x);
				updateTitle(sg, chosen.size());
				return true;
			}
			return false;
		};
		BiFunction<SlotGuiInterface, E, Boolean> deselect = (sg,x)->{
			chosen.remove(x);
			updateTitle(sg, chosen.size());
			return true;
		};
		for (E e : choices) {
			builder.element(ElementHelper.getSelectable(e, toStackFunc, select, deselect, chosen.contains(e)));
		}

		var g = builder.build(gui.getPlayer());
		updateTitle(g, chosen.size());
		g.open(gui);
		return false;
	}

	private void updateTitle(SlotGuiInterface gui, int chosenSize) {
		if (max>0) {
			gui.setTitle(question.shallowCopy().append(" "+chosenSize+"/"+max+" Chosen"));
		} else {
			gui.setTitle(question.shallowCopy().append(" "+chosenSize+" Chosen"));
		}
	}
}