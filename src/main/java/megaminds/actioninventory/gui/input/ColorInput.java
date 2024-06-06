package megaminds.actioninventory.gui.input;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import lombok.Getter;
import megaminds.actioninventory.gui.BetterGui;
import megaminds.actioninventory.gui.BetterGuiI;
import megaminds.actioninventory.util.ElementHelper;
import megaminds.actioninventory.util.GuiHelper;
import megaminds.actioninventory.gui.input.ColorInput.Color;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;

public class ColorInput extends InputCallback<Color> {
	private static final List<Color> COLORS;

	@Override
	public InputCallback<Color> copy() {
		return new ColorInput()
				.question(question)
				.defaultAnswer(defaultAnswer)
				.onFinish(onFinish);
	}

	@Override
	public InputCallback<Color> defaultAnswer(Color defaultAnswer) {
		this.defaultAnswer = Objects.requireNonNullElse(defaultAnswer, COLORS.get(0));
		return this;
	}

	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		Color custom = new Color(0);
		Color selected = COLORS.get(0).copy();
		ItemStack[] selectedEl = new ItemStack[1];

		var g = new BetterGui(GuiHelper.getHandler(5), gui.getPlayer(), false);
		g.setChained(true);
		for (int i = 0, l = COLORS.size(); i < l; i++) {
			g.setSlot(i, colorElement(COLORS.get(i), selected, selectedEl));
		}
		selectedEl[0] = ElementHelper.addGlint(g.getSlot(0).getItemStack());
		g.setSlot(29, colorElement(custom, selected, selectedEl));
		g.setSlot(31, dyeElement("Custom Red", Items.RED_DYE, custom, selected, selectedEl, Color::r, Color::r));
		g.setSlot(33, dyeElement("Custom Green", Items.GREEN_DYE, custom, selected, selectedEl, Color::g, Color::g));
		g.setSlot(35, dyeElement("Custom Blue", Items.BLUE_DYE, custom, selected, selectedEl, Color::b, Color::b));
		g.setSlot(40, ElementHelper.getConfirm((i2,t2,a2,g2)->{onFinish.onFinish(index, type, action, gui, selected);}));
		g.setSlot(42, ElementHelper.getCancel());

		g.open(gui);
		return false;
	}

	private GuiElementInterface dyeElement(String name, Item item, Color custom, Color selected, ItemStack[] selectedEl, Function<Color, Integer> def, BiConsumer<Color, Integer> finish) {
		var current = def.apply(custom);
		var stack = ElementHelper.addLore(ElementHelper.stackOf(item, name), new LiteralText(current+""));
		return new GuiElement(stack, new IntInput()
				.boundedPostFilter(0, 255)
				.defaultAnswer(current)
				.onFinish((i,t,a,g,e)->{
					finish.accept(custom, e);
					((GuiElement)g.getSlot(29)).getItemStack().getOrCreateSubNbt(ItemStack.DISPLAY_KEY).putInt(ItemStack.COLOR_KEY, custom.color);
					g.setSlot(i, dyeElement(name, item, custom, selected, selectedEl, def, finish));
				})
				.question(new LiteralText("Enter "+name+" Amount")));
	}

	private ItemStack colorStack(Color color) {
		var stack = ElementHelper.stackOf(Items.LEATHER_CHESTPLATE, color.name);
		stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).putInt(ItemStack.COLOR_KEY, color.color);
		return stack;
	}

	private GuiElementInterface colorElement(Color color, Color selected, ItemStack[] selectedEl) {
		var stack = colorStack(color);
		return new GuiElement(stack, (i,t,a,g)->{
			selected.set(color);
			ElementHelper.removeEnchants(selectedEl[0]);
			selectedEl[0] = ElementHelper.addGlint(stack);
		});
	}

	@Getter
	public static class Color {
		private int color;
		private String name;
		public Color(TextColor color) {
			if (color==null) return;
			set(color.getRgb(), color.getName());
		}
		public Color(int color, String name) {
			set(color, name);
		}
		public Color(int color) {
			set(color);
		}
		public void set(Color color) {
			if (color==null) return;
			set(color.color, color.name);
		}
		public void set(int color, String name) {
			this.color = color;
			this.name = Objects.requireNonNullElse(name, "Custom");
		}
		public void set(int color) {
			set(color, null);
		}
		public TextColor toTextColor() {
			TextColor c = TextColor.parse(name.toUpperCase().replace(' ', '_'));
			return c!=null ? c : TextColor.fromRgb(color);
		}
		public Color copy() {
			return new Color(color, name);
		}
		public int r() {return (color >> 16) & 0xFF;}
		public int g() {return (color >> 8) & 0xFF;}
		public int b() {return color & 0xFF;}

		public void r(int r) {
			color = ((r & 0xFF) << 16) |
					((g() & 0xFF) << 8) |
					(b() & 0xFF);
		}
		public void g(int g) {
			color = ((r() & 0xFF) << 16) |
					((g & 0xFF) << 8) |
					(b() & 0xFF);
		}
		public void b(int b) {
			color = ((r() & 0xFF) << 16) |
					((g() & 0xFF) << 8) |
					(b & 0xFF);
		}
		@Override
		public String toString() {
			return name;
		}
	}

	static {
		COLORS = List.of(new Color(0x000000, "Black"),
				new Color(0x0000AA, "Dark Blue"),
				new Color(0x00AA00, "Dark Green"),
				new Color(0x00AAAA, "Dark Aqua"),
				new Color(0xAA0000, "Dark Red"),
				new Color(0xAA00AA, "Dark Purple"),
				new Color(0xFFAA00, "Gold"),
				new Color(0xAAAAAA, "Gray"),
				new Color(0x555555, "Dark Grey"),
				new Color(0x5555FF, "Blue"),
				new Color(0x55FF55, "Green"),
				new Color(0x55FFFF, "Aqua"),
				new Color(0xFF5555, "Red"),
				new Color(0xFF55FF, "Light Purple"),
				new Color(0xFFFF55, "Yellow"),
				new Color(0xFFFFFF, "White"),
				new Color(0x835432, "Brown"),
				new Color(0x8932B8, "Purple"),
				new Color(0x9D9D97, "Light Gray"),
				new Color(0xF38BAA, "Pink"),
				new Color(0x80C71F, "Lime"),
				new Color(0x3AB3DA, "Light Blue"),
				new Color(0xC74EBD, "Magenta"),
				new Color(0xF9801D, "Orange"));
	}
}