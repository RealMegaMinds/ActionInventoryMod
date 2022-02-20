package megaminds.actioninventory.gui.elements;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import lombok.Getter;
import net.minecraft.screen.slot.Slot;

@Getter
public class Element {
	private GuiElementInterface el;
	private Slot redirect;
	public Element() {}
	public Element(GuiElementInterface el) {this.el=el;}
	public Element(Slot redirect) {this.redirect=redirect;}
	public void setRedirect(Slot redirect) {this.redirect=redirect;el=null;}
	public void setElement(GuiElementInterface el) {this.el=el;redirect=null;}
	public void set(Element el) {
		this.el = el.el;
		this.redirect = el.redirect;
	}
}