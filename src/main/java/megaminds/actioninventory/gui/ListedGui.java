package megaminds.actioninventory.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import megaminds.actioninventory.gui.elements.Element;
import megaminds.actioninventory.util.ElementHelper;
import megaminds.actioninventory.util.GuiHelper;
import megaminds.actioninventory.util.Helper;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class ListedGui extends BaseGui implements NavigableGui {
	private static final int WIDTH = 9;

	private final List<Element> elements;
	private final List<Consumer<NavigableGui>> changePageCallbacks;
	private final Element[] controlBar;

	private int currentPage;
	private int pageCount;

	public ListedGui(int rowCount, ServerPlayerEntity player, boolean includePlayerInventory) {
		super(GuiHelper.getHandler(rowCount), player, includePlayerInventory);
		this.elements = new ArrayList<>();
		this.changePageCallbacks = new ArrayList<>();
		this.controlBar = new Element[WIDTH];
		controlBar[0] = Element.create(ElementHelper.getFirst());
		controlBar[0].getElement().onAdded(this);
		controlBar[1] = Element.create(ElementHelper.getPrevious());
		controlBar[1].getElement().onAdded(this);
		controlBar[WIDTH-2] = Element.create(ElementHelper.getNext());
		controlBar[WIDTH-2].getElement().onAdded(this);
		controlBar[WIDTH-1] = Element.create(ElementHelper.getLast());
		controlBar[WIDTH-1].getElement().onAdded(this);
		updateSizes();
	}

	@Override
	public void onOpen() {
		goTo(0);
	}

	private void updateSizes() {
		var size = elements.size();
		var temp = size/getSize() + (size%getSize()==0 ? 0 : 1);
		if (temp!=pageCount) {
			pageCount = temp;
			goTo(currentPage);
			reOpen();
		}
	}

	public void setControlSlot(int index, GuiElementInterface element) {
		setControlElement(index, Element.create(element));
	}

	public void setControlRedirect(int index, Slot slot) {
		setControlElement(index, Element.create(slot));
		if (slot!=null) setRedirectingSlots();
	}

	public void setControlElement(int index, Element element) {
		if (index>WIDTH-2 || index<0) return;
		Element.get(controlBar[index]).getElement().onRemoved(this);
		controlBar[index] = element;
		Element.get(element).getElement().onAdded(this);
		updateSlot(getSize()-WIDTH+index);
	}

	@Override
	public void setElement(int index, Element element) {
		var actual = index + getSize()*currentPage;
		if (actual<elements.size() && index<getSize()-WIDTH) {
			Element.get(elements.set(actual, element)).getElement().onRemoved(this);
			Element.get(element).getElement().onRemoved(this);
			updateSlot(index);
		}
	}

	@Override
	public void addElement(Element element) {
		elements.add(element);
		updateSizes();
	}

	/*
	 * Remove the element at the index
	 */
	public void removeSlot(int index) {
		var actual = index + getSize()*currentPage;
		if (actual<elements.size() && index<getSize()-WIDTH) {
			elements.remove(actual);
		}
		updateSizes();
	}

	public int getAbsoluteSize() {
		return elements.size();
	}

	@Override
	public Element getElement(int index) {
		if (index>=getSize()) return Element.EMPTY;
		if (index>=getSize()-WIDTH) return Objects.requireNonNullElse(controlBar[index%WIDTH], Element.EMPTY);

		index += getSize()*currentPage;
		if (index>=elements.size()) return Element.EMPTY;
		return Element.get(elements.get(index));
	}

	@Override
	public void goTo(int i) {
		currentPage = Helper.maxOrMin(i, pageCount-1, 0);
		changePageCallbacks.forEach(c->c.accept(this));
	}

	@Override
	public int getCurrent() {
		return currentPage;
	}

	@Override
	public int getCount() {
		return pageCount;
	}

	@Override
	public void addCallback(Consumer<NavigableGui> onNavigate) {
		if (onNavigate!=null) changePageCallbacks.add(onNavigate);
	}

	@Override
	public void removeCallback(Consumer<NavigableGui> onNavigate) {
		changePageCallbacks.remove(onNavigate);
	}
}