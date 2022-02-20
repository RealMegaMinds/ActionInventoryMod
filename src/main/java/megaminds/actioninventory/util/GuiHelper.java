package megaminds.actioninventory.util;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import megaminds.actioninventory.gui.BetterAnvilGui;
import megaminds.actioninventory.gui.BetterGui;
import megaminds.actioninventory.gui.BetterGuiI;
import megaminds.actioninventory.gui.PagedGui;
import megaminds.actioninventory.gui.PagedGui.PagedGuiBuilder;
import megaminds.actioninventory.gui.callback.BetterClickCallback;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

public class GuiHelper {
	private static final Pattern NON_NUMBER = Pattern.compile("\\D");
	
	public static ScreenHandlerType<?> getHandler(int rows) {
		if (rows<1) throw new IllegalArgumentException("Rows cannot be less than 1.");
		if (rows>6) throw new IllegalArgumentException("Rows cannot be greater than 6.");
		
		return switch (rows) {
		case 1 -> ScreenHandlerType.GENERIC_9X1;
		case 2 -> ScreenHandlerType.GENERIC_9X2;
		case 3 -> ScreenHandlerType.GENERIC_9X3;
		case 4 -> ScreenHandlerType.GENERIC_9X4;
		case 5 -> ScreenHandlerType.GENERIC_9X5;
		case 6 -> ScreenHandlerType.GENERIC_9X6;
		default -> throw new IllegalArgumentException("Unexpected value: " + rows);
		};
	}
		
	public static BetterClickCallback getInt(Text question, int defaultAnswer, Int2IntFunction filter, Int2IntFunction postFilter, IntConsumer onFinish) {
		return getString(question, ""+defaultAnswer, s-> {
			return "" + getInt(s, filter);
		}, s-> {
			return "" + getInt(s, postFilter);
		}, s->onFinish.accept(Integer.parseInt(s)));
	}
	
	private static int getInt(String s, Int2IntFunction filter) {
		s = NON_NUMBER.matcher(s).replaceAll("");
		int x = s.isBlank() ? 0 : Integer.parseInt(s);
		return filter!=null ? filter.applyAsInt(x) : x;
	}
	
	public static BetterClickCallback getString(Text question, String defaultAnswer, Function<String, String> filter, Function<String, String> postFilter, Consumer<String> onFinish) {
		return (i,t,a,g) -> {
			BetterAnvilGui input = new BetterAnvilGui(g.getPlayer(), false);
			input.setDefaultInputValue(defaultAnswer);
			input.setTitle(question);
			input.setChained(true);
			input.setFilter(filter);
			input.setPostFilter(postFilter);
			input.setOnFinish(onFinish);
			input.open(g);
			return false;
		};
	}
	
	public static BetterClickCallback getBoolean(Text question, BooleanConsumer onFinish) {
		return (i,t,a,g) -> {
			BetterGui gui = new BetterGui(ScreenHandlerType.GENERIC_9X1, g.getPlayer(), false);
			gui.setChained(true);
			gui.setSlot(1, ElementHelper.getDone((i2,t2,a2,g2)->onFinish.accept(true), "True"));
			gui.setSlot(3, ElementHelper.getDone((i2,t2,a2,g2)->onFinish.accept(false), "False"));
			gui.setSlot(7, ElementHelper.getCancel(null));
			gui.open(g);
			return false;
		};
	}
	
	private static <E> BetterClickCallback getOptionsSafe(Text question, List<E> choices, Function<E, GuiElementInterface> toEl, Consumer<E> onFinish) {
		return (i,t,a,g) -> {
			int l = choices.size();
			int r = l/9 + (l%9==0?0:1);
			
			BiConsumer<Integer, E> adder;
			Consumer<BetterGuiI> opener;
			if (r<=10) {
				BetterGui gui = new BetterGui(getHandler(r), g.getPlayer(), r>6);
				gui.setChained(true);
				gui.setTitle(question);
				adder = (j,x)->gui.setSlot(j, toEl.apply(x));
				opener = gui::open;
			} else {
				PagedGuiBuilder b = PagedGui.builder(r, false)
						.chained()
						.enableAutoNext(true)
						.title(question);
				adder = (j,x)->b.element(toEl.apply(x));
				opener = q->b.build(q.getPlayer()).open(q);
			}
			for (int j = 0; j < l; j++) {
				adder.accept(j, choices.get(j));
			}
			opener.accept(g);
			return false;
		};
	}
	
	public static <E> BetterClickCallback getOptions(Text question, List<E> choices, Function<E, GuiElementInterface> toEl, Consumer<E> onFinish) {
		return getOptionsSafe(question, choices, e ->ElementHelper.getDelegate(toEl.apply(e), ElementHelper.combine((i2,t2,a2,g2)->onFinish.accept(e), ElementHelper.CLOSE), true), onFinish);
	}
	
	public static <E> BetterClickCallback getOptionsDefStack(Text question, List<E> choices, Function<E, String> toStr, Consumer<E> onFinish) {		
		return getOptionsSafe(question, choices, e -> ElementHelper.getDone((i2,t2,a2,g2)->onFinish.accept(e), toStr.apply(e)), onFinish);
	}
}