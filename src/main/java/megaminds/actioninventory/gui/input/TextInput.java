package megaminds.actioninventory.gui.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.gui.BetterGui;
import megaminds.actioninventory.gui.BetterGuiI;
import megaminds.actioninventory.gui.input.ColorInput.Color;
import megaminds.actioninventory.util.ElementHelper;
import megaminds.actioninventory.util.GuiHelper;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.gui.input.TextInput.TextArgs;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TextInput extends InputCallback<TextArgs> {
	@Override
	public InputCallback<TextArgs> copy() {
		return new TextInput()
				.question(question)
				.defaultAnswer(defaultAnswer)
				.onFinish(onFinish);
	}

	@Override
	public InputCallback<TextArgs> defaultAnswer(TextArgs defaultAnswer) {
		this.defaultAnswer = Objects.requireNonNullElse(defaultAnswer, new TextArgs());
		return this;
	}

	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		BetterGuiI bg = new BetterGui(GuiHelper.getHandler(3), gui.getPlayer(), false);
		bg.setChained(true);
		bg.setTitle(question);

		final TextArgs text = defaultAnswer.copy();

		bg.setSlot(0, new GuiElement(ElementHelper.setLore(ElementHelper.stackOf(Items.SUSPICIOUS_STEW, "Type"), loreText(text.type)), new ChoiceInput<TextArgs.TextType>()
				.choices(TextArgs.TextType.values())
				.question(new LiteralText("Text Type"))
				.defaultAnswer(Set.of(text.type))
				.onFinish((i,t,a,g,e)->{
					var x = Helper.getAny(e);
					if (x!=null) {
						text.type = x;
						updateLoreAndCallback(g, i, e, x);
					}
				})));
		bg.setSlot(1, new GuiElement(ElementHelper.setLore(ElementHelper.stackOf(Items.PAPER, "Text"), loreText(text.text)), new StringInput()
				.question(new LiteralText("Actual Text"))
				.defaultAnswer(text.text)
				.onFinish((i,t,a,g,e)->{
					text.text = e;
					updateLoreAndCallback(g, i, e);
				})));
		bg.setSlot(2, new GuiElement(ElementHelper.setLore(ElementHelper.stackOf(Items.BOOK, "Siblings"), loreText(text.siblings.size())), new ListInput<>(new TextInput()
				.question(new LiteralText("Text Sibling")))
				.toStackFunc(e->ElementHelper.stackOf(Items.WHITE_WOOL, e.toText()))
				.question(new LiteralText("Text's Siblings"))
				.defaultAnswer(text.siblings)
				.onFinish((i,t,a,g,e)->{
					text.siblings = new ArrayList<>(e);
					updateLoreAndCallback(g, i, e, e.size());
				})));

		bg.setSlot(4, new GuiElement(ElementHelper.setLore(ElementHelper.stackOf(Items.RED_DYE, "Color"), loreText(text.color)), new ColorInput()
				.question(new LiteralText("Text Color"))
				.defaultAnswer(text.color)
				.onFinish((i,t,a,g,e)->{
					text.color = e.copy();
					updateLoreAndCallback(g, i, e);
				})));
		bg.setSlot(5, new GuiElement(ElementHelper.setLore(ElementHelper.stackOf(Items.INK_SAC, "Bold"), loreText(text.bold)), new BooleanInput()
				.question(new LiteralText("Make Text Bold?"))
				.defaultAnswer(text.bold)
				.onFinish((i,t,a,g,e)->{
					text.bold = e;
					updateLoreAndCallback(g, i, e);
				})));
		bg.setSlot(6, new GuiElement(ElementHelper.setLore(ElementHelper.stackOf(Items.POLISHED_BLACKSTONE_WALL, "Italic"), loreText(text.italic)), new BooleanInput()
				.question(new LiteralText("Make Text Italic?"))
				.defaultAnswer(text.italic)
				.onFinish((i,t,a,g,e)->{
					text.italic = e;
					updateLoreAndCallback(g, i, e);
				})));
		bg.setSlot(7, new GuiElement(ElementHelper.setLore(ElementHelper.stackOf(Items.POLISHED_BLACKSTONE_PRESSURE_PLATE, "Underlined"), loreText(text.underlined)), new BooleanInput()
				.question(new LiteralText("Make Text Underlined?"))
				.defaultAnswer(text.underlined)
				.onFinish((i,t,a,g,e)->{
					text.underlined = e;
					updateLoreAndCallback(g, i, e);
				})));
		bg.setSlot(8, new GuiElement(ElementHelper.setLore(ElementHelper.stackOf(Items.RAIL, "Strikethrough"), loreText(text.strikethrough)), new BooleanInput()
				.question(new LiteralText("Make Text Strikethrough?"))
				.defaultAnswer(text.strikethrough)
				.onFinish((i,t,a,g,e)->{
					text.strikethrough = e;
					updateLoreAndCallback(g, i, e);
				})));
		bg.setSlot(16, new GuiElement(ElementHelper.setLore(ElementHelper.stackOf(Items.BLACK_STAINED_GLASS_PANE, "Obfuscated"), loreText(text.obfuscated)), new BooleanInput()
				.question(new LiteralText("Make Text Obfuscated?"))
				.defaultAnswer(text.obfuscated)
				.onFinish((i,t,a,g,e)->{
					text.obfuscated = e;
					updateLoreAndCallback(g, i, e);
				})));
		bg.setSlot(17, new GuiElement(ElementHelper.setLore(ElementHelper.stackOf(Items.FEATHER, "Font"), loreText(text.font)), new IdentifierInput()
				.question(new LiteralText("Text Font ID"))
				.defaultAnswer(text.font)
				.onFinish((i,t,a,g,e)->{
					text.font = e;
					updateLoreAndCallback(g, i, e);
				})));

		bg.setSlot(21, ElementHelper.getConfirm((i,t,a,g)->onFinish.onFinish(index, type, action, gui, text)));
		bg.setSlot(23, ElementHelper.getCancel());

		bg.open(gui);
		return false;
	}

	private static <E> void updateLoreAndCallback(SlotGuiInterface gui, int index, E e) {
		updateLoreAndCallback(gui, index, e, e);
	}

	@SuppressWarnings("unchecked")
	private static <E> void updateLoreAndCallback(SlotGuiInterface gui, int index, E e, Object o) {
		var g = gui.getSlot(index);
		ElementHelper.setLore(g.getItemStack(), loreText(o));
		((InputCallback<E>)g.getGuiCallback()).defaultAnswer(e);
	}

	private static Text loreText(Object o) {
		return o==null ? new LiteralText("Unset").styled(y->y.withColor(Formatting.RED)) : new LiteralText(o.toString());
	}

	public static class TextArgs {
		public enum TextType {Literal, Translatable}

		public Color color;
		public Boolean bold;
		public Boolean italic;
		public Boolean underlined;
		public Boolean strikethrough;
		public Boolean obfuscated;
		public Identifier font;

		public String text = "";
		public List<TextArgs> siblings = new ArrayList<>();
		public TextType type = TextType.Literal;
		public TextArgs() {}

		public TextArgs(Text toCopy) {
			if (toCopy==null || toCopy==LiteralText.EMPTY) return;
			if (toCopy instanceof TranslatableText t) {
				this.type = TextType.Translatable;
				this.text = t.getKey();
			} else if (toCopy instanceof LiteralText l) {
				this.type = TextType.Literal;
				this.text = l.getRawString();
			} else {
				throw new IllegalArgumentException("Type of text can only be translatable or literal: "+toCopy);
			}

			var s = toCopy.getStyle();
			color = new Color(s.getColor());
			bold = s.isBold();
			italic = s.isItalic();
			underlined = s.isUnderlined();
			strikethrough = s.isStrikethrough();
			obfuscated = s.isObfuscated();
			font = s.getFont();
			toCopy.getSiblings().stream().map(TextArgs::new).forEach(siblings::add);
		}

		public TextArgs copy() {
			var t = new TextArgs();
			t.type = type;
			t.text = text;
			t.color = color==null?null:color.copy();
			t.bold = bold;
			t.italic = italic;
			t.underlined = underlined;
			t.strikethrough = strikethrough;
			t.obfuscated = obfuscated;
			t.font = font;
			siblings.stream().map(TextArgs::copy).forEach(t.siblings::add);
			return t;
		}

		public Text toText() {
			var t = type==TextType.Literal ? new LiteralText(text) : new TranslatableText(text);
			t.setStyle(Style.EMPTY
					.withColor(color==null?null:color.toTextColor())
					.withBold(bold)
					.withItalic(italic)
					.withUnderline(underlined)
					.withStrikethrough(strikethrough)
					.withObfuscated(obfuscated)
					.withFont(font));
			for (var s : siblings) t.append(s.toText());
			return t;
		}
	}
}