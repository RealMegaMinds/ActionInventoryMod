package megaminds.actioninventory.gui;

import megaminds.actioninventory.gui.elements.Element;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public class BetterGui extends BaseGui {
	private Element[] elements;

	public BetterGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots) {
		super(type, player, includePlayerInventorySlots);
		elements = new Element[getSize()];
	}

	@Override
	public Element getElement(int index) {
		if (!inBounds(index)) return Element.EMPTY;
		return elements[index];
	}

	@Override
	public void addElement(Element element) {
		var i = getFirstEmptySlot();
		if (i>=0) setElement(i, element);
	}

	@Override
	public void setElement(int index, Element element) {
		if (inBounds(index)) {
			Element.get(elements[index]).getElement().onRemoved(this);
			elements[index] = element;
			Element.get(element).getElement().onAdded(this);
		}
	}
}