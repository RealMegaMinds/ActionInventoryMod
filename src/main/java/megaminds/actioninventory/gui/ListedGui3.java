package megaminds.actioninventory.gui;

import java.util.Objects;
import megaminds.actioninventory.gui.elements.Element;
import megaminds.actioninventory.misc.DefaultedList;
import megaminds.actioninventory.util.ElementHelper;
import net.minecraft.server.network.ServerPlayerEntity;

public class ListedGui3 extends ListedGui2 {
	private final DefaultedList<Element> elements;

	public ListedGui3(int rowCount, ServerPlayerEntity player, boolean includePlayerInventory) {
		super(rowCount, player, includePlayerInventory);
		this.elements = new DefaultedList<>(Element.EMPTY);
		
		var el = Element.create(ElementHelper.getFirst());
		controlBar.set(0, el);
		el.getElement().onAdded(this);
		el = Element.create(ElementHelper.getPrevious());
		controlBar[1].getElement().onAdded(this);
		el = Element.create(ElementHelper.getNext());
		controlBar[WIDTH-2].getElement().onAdded(this);
		el = Element.create(ElementHelper.getLast());
		controlBar[WIDTH-1].getElement().onAdded(this);
		updateSizes();
	}

	@Override
	public boolean isRestrictedControlIndex(int index) {
		return index>=WIDTH-2 || index<2;
	}

	private void updateSizes() {
		var size = elements.size();
		goTo(size/getSize() + (size%getSize()==0 ? 0 : 1));
	}

	@Override
	protected void updateControlSlot(int index) {
		updateSlot(getSize()-WIDTH+index);
	}

	@Override
	public void setElement(int index, Element element) {
		var corrected = correctIndex(index);
		if (isValidIndex(index, corrected)) {
			elements.set(corrected, element).getElement().onRemoved(this);
			elements.get(corrected).getElement().onRemoved(this);
			updateSlot(index);
		}
	}

	private int correctIndex(int index) {
		return index + getSize()*getCurrent();
	}

	private boolean isValidIndex(int index, int corrected) {
		return corrected<elements.size() && index<getSize()-WIDTH;
	}

	@Override
	public void addElement(Element element) {
		elements.add(element);
		updateSizes();
	}

	public void removeElement(int index) {
		var corrected = correctIndex(index);
		if (isValidIndex(index, corrected)) {
			elements.remove(corrected).getElement().onRemoved(this);
		}
		updateSizes();
	}

	/**
	 * This can change.
	 */
	@Override
	public int getAbsoluteSize() {
		return elements.size();
	}

	@Override
	public Element getElement(int index) {
		var size = getSize();
		if (index>=size) return Element.EMPTY;
		if (index>=size-WIDTH) return Objects.requireNonNullElse(controlBar[index%WIDTH], Element.EMPTY);

		var corrected = correctIndex(index);
		if (corrected>=elements.size()) return Element.EMPTY;
		return elements.get(corrected);
	}
}