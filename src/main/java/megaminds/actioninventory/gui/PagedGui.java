//package megaminds.actioninventory.gui;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.OptionalInt;
//import eu.pb4.sgui.api.GuiHelpers;
//import eu.pb4.sgui.api.elements.GuiElement;
//import eu.pb4.sgui.api.elements.GuiElementBuilderInterface;
//import eu.pb4.sgui.api.elements.GuiElementInterface;
//import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
//import eu.pb4.sgui.api.elements.GuiElementInterface.ItemClickCallback;
//import eu.pb4.sgui.virtual.inventory.VirtualScreenHandler;
//import eu.pb4.sgui.virtual.inventory.VirtualScreenHandlerFactory;
//import eu.pb4.sgui.virtual.inventory.VirtualSlot;
//import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
//import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.Setter;
//import megaminds.actioninventory.gui.elements.Element;
//import megaminds.actioninventory.util.ElementHelper;
//import megaminds.actioninventory.util.GuiHelper;
//import megaminds.actioninventory.util.Helper;
//import net.minecraft.item.ItemStack;
//import net.minecraft.screen.ScreenHandlerType;
//import net.minecraft.screen.slot.Slot;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.util.Identifier;
//
//public class PagedGui implements BetterGuiI {
//	private static final int WIDTH = 9;
//
//	private final Int2ObjectMap<Element> elements;
//	private final Element[] controlBar;
//	private final ServerPlayerEntity player;
//
//	private ScreenHandlerType<?> type;
//	private boolean includingPlayer;
//	//doesn't include player
//	private int guiHeight;
//	private int guiSize;
//	//includes player
//	private int pageHeight;	
//	private int pageSize;	//size of a single page
//	private int lastIndex;
//
//	private int pageCount;
//
//	private boolean autoHeight;	//automatically increases the height
//	private boolean autoPage;	//automatically adds pages
//
//	private BetterGuiI previousGui;
//	private boolean chained;
//	private Identifier id;
//
//	private boolean lockPlayerInventory;
//	private boolean autoUpdate = true;
//	private boolean redirectingSlots;
//	private Text title;
//
//	private boolean controlBarOnTop;
//
//	private int currentPage;
//	private boolean open;
//	private int syncId = -1;
//	private VirtualScreenHandler screenHandler;
//	private boolean reOpen;
//
//	private PagedGui(ServerPlayerEntity player) {
//		this.player = player;
//		this.elements = new Int2ObjectOpenHashMap<>();
//		this.controlBar = new Element[WIDTH];
//		Arrays.fill(controlBar, Element.EMPTY);
//	}
//
//	private void addElement(Element e) {
//
//	}
//
//	private void setIncludingPlayer(boolean includePlayer) {
//		if (includingPlayer != includePlayer) {
//			includingPlayer = includePlayer;
//			pageHeight = guiHeight + (includingPlayer ? 4 : 0);
//			pageSize = WIDTH*pageHeight;
//			reOpen();
//		}
//	}
//
//	private void setHeight(int height) {
//		var temp = Helper.maxOrMin(height+1, 6, 1);
//		if (guiHeight!=temp) {
//			guiHeight = temp;
//			pageHeight = guiHeight + (includingPlayer ? 4 : 0);
//			pageSize = WIDTH * pageHeight;
//			guiSize = WIDTH*guiHeight;
//			type = GuiHelper.getHandler(guiHeight);
//			setIncludingPlayer(includingPlayer);
//			reOpen();
//		}
//	}
//
//	//	private void addStandardControlElements() {
//	//		System.out.println("pages:"+pages.size());
//	//		if (standardElementsAdded || pages.size()<2) return;
//	//		System.out.println("added");
//	//		if (controlBar[0]==Element.EMPTY) {
//	//			setControlSlot(0, ElementHelper.getFirst());
//	//		} else {
//	//			System.out.println(controlBar[0]);
//	//		}
//	//		if (controlBar[1]==Element.EMPTY) setControlElement(1, ElementHelper.getPrevious());
//	//		if (controlBar[7]==Element.EMPTY) setControlElement(7, ElementHelper.getNext());
//	//		if (controlBar[8]==Element.EMPTY) setControlSlot(8, ElementHelper.getLast());
//	//		standardElementsAdded = true;
//	//	}
//
//	public void setControlSlot(int index, GuiElementInterface element) {
//		if (index<0 || index>=9) return;
//		setControlElement(index, Element.create(element));
//	}
//
//	public void setControlElement(int index, Element element) {
//		if (index<0 || index>=9) return;
//		controlBar[index].getElement().onRemoved(this);
//		controlBar[index] = element;
//		element.getElement().onAdded(this);
//		element.update(this);
//		if (element.getRedirect()!=null) redirectingSlots = true;
//		updateSlot(index);
//	}
//
//	public int getWidth() {
//		return WIDTH;
//	}
//
//	public void newPage(boolean changeToNewPage) {
//		int index = pages.size();
//		pages.add(new Element[workingSize]);
//		Arrays.fill(pages.get(index), Element.EMPTY);
//		addStandardControlElements();
//		if (changeToNewPage) setCurrentPage(index);
//		reOpen();
//	}
//
//	public void setCurrentPage(int i) {
//		int corrected = Helper.maxOrMin(i, pages.size()-1, 0);
//		if (corrected!=currentPage) {
//			currentPage = corrected;
//			for (Element e : controlBar) {
//				e.update(this);
//			}
//			reOpen();
//		}
//	}
//
//	@Override
//	public boolean getAutoUpdate() {
//		return autoUpdate;
//	}
//
//	@Override
//	public boolean getLockPlayerInventory() {
//		return lockPlayerInventory || includingPlayer;
//	}
//
//	@Override
//	public boolean open(BetterGuiI previous) {
//		this.previousGui = previous;
//		return open();
//	}
//
//	@Override
//	public void clearPrevious() {
//		previousGui = null;
//	}
//
//	@Override
//	public boolean reOpen() {
//		if (isOpen()) return sendGui();
//		return false;
//	}
//
//	@Override
//	public void setTitle(Text title) {
//		this.title = title;
//		reOpen();
//	}
//
//	private boolean sendGui() {
//		reOpen = true;
//		OptionalInt temp = this.player.openHandledScreen(new VirtualScreenHandlerFactory(this));
//		reOpen = false;
//		if (temp.isPresent()) {
//			this.syncId = temp.getAsInt();
//			if (this.player.currentScreenHandler instanceof VirtualScreenHandler v && v.getGui()==this) {
//				this.screenHandler = v;
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Override
//	public boolean open() {
//		if (player.isDisconnected() || open) {
//			return false;
//		} else {
//			onOpen();
//			open = true;
//			return this.sendGui();
//		}
//	}
//
//	@Override
//	public void onOpen() {
//		setCurrentPage(0);
//	}
//
//	@Override
//	public void close(boolean screenHandlerIsClosed) {
//		if (this.open && !this.reOpen) {
//			this.open = false;
//			this.reOpen = false;
//
//			if (!screenHandlerIsClosed && this.player.currentScreenHandler == this.screenHandler) {
//				this.player.closeHandledScreen();
//			}
//
//			GuiHelpers.sendPlayerInventory(this.getPlayer());
//			this.onClose();
//		} else {
//			this.reOpen = false;
//		}
//	}
//
//	private static boolean inBounds(int i, int size) {
//		return i>=0 && i<size;
//	}
//
//	private boolean isControlIndex(int index) {
//		return controlBarOnTop ? index<WIDTH : index>=size-WIDTH;
//	}
//
//	@Override
//	public GuiElementInterface getSlot(int index) {
//		if (!inBounds(index, size)) return GuiElement.EMPTY;
//		if (isControlIndex(index)) return controlBar[index%9].getElement();
//		if (controlBarOnTop) index -= WIDTH;
//		return pages.get(currentPage)[index].getElement();
//	}
//
//	@Override
//	public Slot getSlotRedirect(int index) {
//		if (!inBounds(index, size)) return null;
//		if (isControlIndex(index)) return controlBar[index%9].getRedirect();
//		if (controlBarOnTop) index -= WIDTH;
//		return pages.get(currentPage)[index].getRedirect();
//	}
//
//	@Override
//	public void setSlot(int index, GuiElementInterface element) {
//		setElement(index, Element.create(element));
//	}
//
//	@Override
//	public void setSlotRedirect(int index, Slot slot) {
//		setElement(index, Element.create(slot));
//	}
//
//	public void setElement(int index, Element e) {
//		if (!inBounds(index, workingSize)) return;
//		if (e==null) {
//			clearSlot(index);
//			return;
//		}
//		var page = pages.get(currentPage);
//		page[index].getElement().onRemoved(this);
//		page[index] = e;
//		e.getElement().onAdded(this);
//		if (e.getRedirect()!=null) redirectingSlots = true;
//		updateSlot(index);
//	}
//
//	@Override
//	public void clearSlot(int index) {
//		if (!inBounds(index, workingSize)) return;
//		var page = pages.get(currentPage);
//		page[index].getElement().onRemoved(this);
//		page[index] = Element.EMPTY;
//		updateSlot(index);
//	}
//
//	@Override
//	public void addSlot(GuiElementInterface element) {
//		addElement(Element.create(element));
//	}
//
//	@Override
//	public void addSlot(ItemStack itemStack) {
//		addSlot(itemStack, GuiElement.EMPTY_CALLBACK);
//	}
//
//	@Override
//	public void addSlot(GuiElementBuilderInterface<?> element) {
//		addSlot(element.build());
//	}
//
//	@Override
//	public void addSlot(ItemStack itemStack, ClickCallback callback) {
//		addSlot(new GuiElement(itemStack, callback));
//	}
//
//	@Override
//	public void addSlot(ItemStack itemStack, ItemClickCallback callback) {
//		addSlot(itemStack, (ClickCallback)callback);
//	}
//
//	@Override
//	public void addSlotRedirect(Slot slot) {
//		addElement(Element.create(slot));
//	}
//
//	public void addElement(Element e) {
//		int i = getFirstEmptySlot();
//		if (i!=-1) {
//			setElement(i, e);
//		} else {
//			newPage(true);
//			addElement(e);
//		}
//	}
//
//	private void updateSlot(int index) {
//		if (this.open && this.autoUpdate && this.screenHandler != null) {
//			this.screenHandler.setSlot(index, new VirtualSlot(this.screenHandler.inventory, index, 0, 0));
//		}
//	}
//
//	@Override
//	public int getFirstEmptySlot() {
//		var page = pages.get(currentPage);
//		for (int i = 0; i < workingSize; i++) {
//			if (page[i]==Element.EMPTY) return i;
//		}
//		return -1;
//	}
//
//	public static int minRows(int size) {
//		return size/9 + (size%9==0?0:1);
//	}
//
//	public static PagedGuiBuilder builder(int rowCount, boolean includePlayerInventorySlots) {
//		return new PagedGuiBuilder(rowCount, includePlayerInventorySlots);
//	}
//
//	public static class PagedGuiBuilder {
//		private final List<Int2ObjectMap<Element>> pages;
//		private final Int2ObjectMap<Element> controlBar;
//		private final boolean includingPlayer;
//		private final int height;
//		private final int workingSize;
//		private boolean chained;
//		private Identifier id;
//		private boolean lockPlayerInventory;
//		private boolean autoUpdate;
//		private Text title;
//		private boolean controlBarOnTop;
//		private boolean standardPageElements;
//
//		private int currentPage;
//		private int currentIndex;
//		private int controlIndex;
//
//		private PagedGuiBuilder(int rowCount, boolean includePlayerInventorySlots) {
//			this.height = Math.min(rowCount, 6);
//			this.workingSize = WIDTH*(height-1)+(includePlayerInventorySlots ? 36 : 0);
//			this.controlBar = new Int2ObjectOpenHashMap<Element>(WIDTH);
//			this.pages = new ArrayList<>();
//			this.includingPlayer = includePlayerInventorySlots;
//		}
//
//		public PagedGui build(ServerPlayerEntity player) {
//			PagedGui gui = new PagedGui(height, player, includingPlayer);
//			gui.setChained(chained);
//			gui.setId(id);
//			gui.setLockPlayerInventory(lockPlayerInventory);
//			gui.setAutoUpdate(autoUpdate);
//			gui.setStandardPageElements(standardPageElements);
//			gui.title = title;
//			gui.setControlBarOnTop(controlBarOnTop);
//			controlBar.forEach(gui::setControlElement);
//			int i = 0;
//			for (int l = pages.size(); i < l; i++) {
//				pages.get(i).forEach(gui::setElement);
//				if (i<l-1) gui.newPage(true);
//			}
//			return gui;
//		}
//
//		public int getSize() {
//			return workingSize;
//		}
//
//		public PagedGuiBuilder standardPageElements() {
//			standardPageElements = true;
//			return this;
//		}
//
//		public PagedGuiBuilder standardInputElements(ClickCallback confirm) {
//			controlBar.put(3, Element.create(ElementHelper.getConfirm(confirm)));
//			controlBar.put(5, Element.create(ElementHelper.getCancel()));
//			return this;
//		}
//
//		public PagedGuiBuilder controlElement(Element e) {
//			if (controlIndex<WIDTH) {
//				if (e!=null && e!=Element.EMPTY) {
//					controlBar.put(controlIndex, e);
//				}
//				controlSkip();
//			}
//			return this;
//		}
//
//		public PagedGuiBuilder controlElement(GuiElementInterface e) {
//			if (controlIndex<WIDTH) {
//				if (e!=null && e!=GuiElement.EMPTY) {
//					controlBar.put(controlIndex, Element.create(e));
//				}
//				controlSkip();
//			}
//			return this;
//		}
//
//		public PagedGuiBuilder controlSkip() {
//			return controlSkip(1);
//		}
//
//		public PagedGuiBuilder controlSkip(int i) {
//			controlIndex += i;
//			return this;
//		}
//
//		public PagedGuiBuilder redirect(Slot redirect) {
//			if (redirect!=null) getOrCreatePage().put(currentIndex, Element.create(redirect));
//			skip();
//			return this;
//		}
//
//		public PagedGuiBuilder element(GuiElementInterface element) {
//			if (element!=null && element!=GuiElement.EMPTY) getOrCreatePage().put(currentIndex, Element.create(element));
//			skip();
//			return this;
//		}
//
//		public PagedGuiBuilder skip() {
//			return skip(1);
//		}
//
//		public PagedGuiBuilder skip(int i) {
//			currentIndex += i;
//			while (currentIndex>=workingSize) {
//				currentPage++;
//				currentIndex -= workingSize;
//			}
//			return this;
//		}
//
//		private Int2ObjectMap<Element> getOrCreatePage() {
//			while (pages.size()<=currentPage) {
//				pages.add(new Int2ObjectOpenHashMap<>(WIDTH));
//			}
//			return pages.get(currentPage);
//		}
//
//		public PagedGuiBuilder elements(List<GuiElementInterface> elements) {
//			if (elements!=null) elements.forEach(this::element);
//			return this;
//		}
//
//		public PagedGuiBuilder newPage() {
//			currentPage++;
//			currentIndex = 0;
//			return this;
//		}
//
//		public PagedGuiBuilder title(Text title) {
//			this.title = title;
//			return this;
//		}
//
//		public PagedGuiBuilder disableAutoUpdate() {
//			autoUpdate = false;
//			return this;
//		}
//
//		public PagedGuiBuilder lockPlayerInventory() {
//			lockPlayerInventory = true;
//			return this;
//		}
//
//		public PagedGuiBuilder id(Identifier id) {
//			this.id = id;
//			return this;
//		}
//
//		public PagedGuiBuilder chained() {
//			chained = true;
//			return this;
//		}
//
//		public PagedGuiBuilder controlBarOnTop() {
//			controlBarOnTop = true;
//			return this;
//		}
//	}
//}