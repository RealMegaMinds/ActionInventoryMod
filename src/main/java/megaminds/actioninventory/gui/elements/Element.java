package megaminds.actioninventory.gui.elements;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import net.minecraft.screen.slot.Slot;

public class Element {
	public static final Element EMPTY = new Element(GuiElement.EMPTY, null);

	private final GuiElementInterface element;
	private final Slot redirect;

	private Element(GuiElementInterface element, Slot redirect) {
		this.element = element;
		this.redirect = redirect;
	}
	
	public static boolean isEmpty(Element e) {
		return e==null || (e.element==GuiElement.EMPTY && e.redirect==null);
	}

	public static Element create(GuiElementInterface element) {
		if (element==null || element==GuiElement.EMPTY) return EMPTY;
		return new Element(element, null);
	}
	public static Element create(Slot redirect) {
		if (redirect==null) return EMPTY;
		return new Element(GuiElement.EMPTY, redirect);
	}

	@NotNull
	public GuiElementInterface getElement() {
		return element;
	}
	@Nullable
	public Slot getRedirect() {
		return redirect;
	}
}