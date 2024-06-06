package megaminds.actioninventory.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import megaminds.actioninventory.gui.elements.Element;
import megaminds.actioninventory.misc.DefaultedList;
import megaminds.actioninventory.util.GuiHelper;
import megaminds.actioninventory.util.Helper;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ListedGui2 extends BaseGui implements NavigableGui {
	public static final int WIDTH = 9;

	protected final List<Consumer<NavigableGui>> changePageCallbacks;
	protected final DefaultedList<Element> controlBar;

	private int currentPage;
	private int pageCount;

	public ListedGui2(int rowCount, ServerPlayerEntity player, boolean includePlayerInventory) {
		super(GuiHelper.getHandler(rowCount), player, includePlayerInventory);
		this.changePageCallbacks = new ArrayList<>();
		this.controlBar = new DefaultedList<>(Element.EMPTY, WIDTH);
	}

	protected abstract void updateControlSlot(int index);

	@Override
	public void onOpen() {
		goTo(0);
	}

	@Override
	public void setControlSlot(int index, GuiElementInterface element) {
		setControlElement(index, Element.create(element));
	}

	@Override
	public void setControlRedirect(int index, Slot slot) {
		setControlElement(index, Element.create(slot));
		if (slot!=null) setRedirectingSlots();
	}

	@Override
	public void setControlElement(int index, Element element) {
		if (isRestrictedControlIndex(index)) return;
		controlBar.get(index).getElement().onRemoved(this);
		controlBar.set(index, element);
		controlBar.get(index).getElement().onAdded(this);
		updateControlSlot(index);
	}

	@Override
	public void goTo(int i) {
		var temp = Helper.maxOrMin(i, pageCount-1, 0);
		if (currentPage!=temp) {
			currentPage = temp;
			changePageCallbacks.forEach(c->c.accept(this));
		}
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