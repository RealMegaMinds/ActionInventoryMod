//package megaminds.actioninventory.gui;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.OptionalInt;
//import java.util.function.IntSupplier;
//
//import eu.pb4.sgui.api.GuiHelpers;
//import eu.pb4.sgui.api.elements.GuiElement;
//import eu.pb4.sgui.api.elements.GuiElementInterface;
//import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
//import eu.pb4.sgui.api.gui.SlotGuiInterface;
//import eu.pb4.sgui.virtual.inventory.VirtualScreenHandler;
//import eu.pb4.sgui.virtual.inventory.VirtualScreenHandlerFactory;
//import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
//import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.Setter;
//import megaminds.actioninventory.gui.elements.Element;
//import megaminds.actioninventory.util.ElementHelper;
//import megaminds.actioninventory.util.GuiHelper;
//import net.minecraft.item.ItemStack;
//import net.minecraft.screen.ScreenHandlerType;
//import net.minecraft.screen.slot.Slot;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.util.Identifier;
//
//@Getter
//@Setter
//public class PagedGui implements SlotGuiInterface, BetterGuiI {
//	private static final int WIDTH = 9;
//
//	@Getter(AccessLevel.NONE)
//	@Setter(AccessLevel.NONE)
//	private final List<Int2ObjectMap<Element>> pages;
//	private final boolean includingPlayer;
//	private final int height;
//	private final int size;
//	/**Excludes player slots*/
//	private final int virtualSize;
//	private final ServerPlayerEntity player;
//	private final ScreenHandlerType<?> type;
//	private final boolean redirectingSlots;
//
//	private int currentPage;
//
//	@Setter(AccessLevel.NONE)
//	private BetterGuiI previousGui;
//	private boolean chained;
//	private Identifier id;
//
//	@Getter(AccessLevel.NONE)
//	private boolean lockPlayerInventory;
//	@Getter(AccessLevel.NONE)
//	private boolean autoUpdate;
//	private Text title;
//
//	@Setter(AccessLevel.NONE)
//	private boolean open;
//	@Setter(AccessLevel.NONE)
//	private int syncId = -1;
//	@Getter(AccessLevel.NONE)
//	@Setter(AccessLevel.NONE)
//	private VirtualScreenHandler screenHandler;
//	@Getter(AccessLevel.NONE)
//	@Setter(AccessLevel.NONE)
//	private boolean reOpen;
//	
//	private PagedGui(ServerPlayerEntity player, List<Int2ObjectMap<Element>> pages, int rowCount, boolean includePlayerInventorySlots, boolean hasRedirects) {
//		this.player = player;
//		this.pages = pages;
//		this.height = rowCount;
//		this.virtualSize = WIDTH*height;
//		this.size = virtualSize+(includePlayerInventorySlots ? 36 : 0);
//		this.type = GuiHelper.getHandler(rowCount);
//		this.includingPlayer = includePlayerInventorySlots;
//		this.redirectingSlots = hasRedirects;
//	}
//	
//	public int getWidth() {
//		return WIDTH;
//	}
//	
//	public void setCurrentPage(int i) {
//		currentPage = i<0 ? 0 : i > size-1 ? size-1 : i;
//		if (isOpen()) sendGui();
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
//		if (isOpen()) sendGui();
//		return false;
//	}
//
//	@Override
//	public void setTitle(Text title) {
//		this.title = title;
//
//		if (this.open) this.sendGui();
//	}
//
//	private boolean sendGui() {
//		reOpen = true;
//		OptionalInt temp = this.player.openHandledScreen(new VirtualScreenHandlerFactory(this));
//		reOpen = false;
//		if (temp.isPresent()) {
//			this.syncId = temp.getAsInt();
//			if (this.player.currentScreenHandler instanceof VirtualScreenHandler) {
//				this.screenHandler = (VirtualScreenHandler) this.player.currentScreenHandler;
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Override
//	public boolean open() {
//		if (this.player.isDisconnected() || this.open) {
//			return false;
//		} else {
//			this.open = true;
//			this.onOpen();
//			return this.sendGui();
//		}
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
//	@Override
//	public GuiElementInterface getSlot(int index) {
//		return getEl(index).map(Element::getEl).orElse(null);
//	}
//
//	@Override
//	public Slot getSlotRedirect(int index) {
//		return getEl(index).map(Element::getRedirect).orElse(null);
//	}
//	
//	private Optional<Element> getEl(int index) {
//		if (!inBounds(index)) return Optional.empty();
//		var page = pages.get(currentPage);
//		if (page==null) return Optional.empty();
//		return Optional.ofNullable(page.get(index));
//	}
//	
//	private boolean inBounds(int i) {
//		return i>=0 && i<size;
//	}
//	
//	public static PagedGuiBuilder builder(int rowCount, boolean includePlayerInventorySlots) {
//		return new PagedGuiBuilder(rowCount, includePlayerInventorySlots);
//	}
//	
//	@Deprecated
//	@Override
//	public void setSlot(int index, GuiElementInterface element) {
//	}
//
//	@Deprecated
//	@Override
//	public void setSlotRedirect(int index, Slot slot) {
//	}
//
//	@Deprecated
//	@Override
//	public void clearSlot(int index) {
//	}
//	
//	@Deprecated
//	@Override
//	public int getFirstEmptySlot() {
//		return -1;
//	}
//
//	public static class PagedGuiBuilder {
//		private final List<Int2ObjectMap<Element>> pages;
//		private final boolean includingPlayer;
//		private final int height;
//		private final int size;
//		private boolean hasRedirects;
//		
//		private boolean chained;
//		private Identifier id;
//		private boolean lockPlayerInventory;
//		private boolean autoUpdate = true;
//		private Text title;
//		
//		private int currentPage;
//		private int currentIndex;
//		private boolean autoNext;
//		
//		private PagedGuiBuilder(int rowCount, boolean includePlayerInventorySlots) {
//			this.includingPlayer = includePlayerInventorySlots;
//			this.height = Math.min(rowCount, 6);
//			this.size = height*WIDTH+(includingPlayer ? 36 : 0);
//			this.pages = new ArrayList<>();
//			createPage();
//		}
//		
//		public PagedGui build(ServerPlayerEntity player) {
//			PagedGui gui = new PagedGui(player, pages, height, includingPlayer, hasRedirects);
//			gui.setChained(chained);
//			gui.setId(id);
//			gui.setLockPlayerInventory(lockPlayerInventory);
//			gui.setAutoUpdate(autoUpdate);
//			gui.title = title;
//			return gui;
//		}
//		
//		/**
//		 * Builder automatically moves to the next page when one is full.
//		 */
//		public PagedGuiBuilder enableAutoNext(boolean auto) {
//			autoNext = auto;
//			return this;
//		}
//		
//		public boolean isFull() {
//			return currentIndex >= size;
//		}
//		
//		public int getSize() {
//			return size;
//		}
//
//		private Optional<Element> getElement() {
//			if (isFull() && autoNext) {
//				nextPage();
//			} else if (isFull()) {
//				return Optional.empty();
//			}
//			return Optional.of(pages.get(currentPage).computeIfAbsent(currentIndex, i->new Element()));
//		}
//		
//		public PagedGuiBuilder redirect(Slot redirect) {
//			if (redirect!=null) {
//				getElement().ifPresent(e->e.setRedirect(redirect));
//				hasRedirects = true;
//			}
//			skip();
//			return this;
//		}
//		
//		public PagedGuiBuilder element(GuiElementInterface element) {
//			if (element!=null) getElement().ifPresent(e->e.setElement(element));
//			skip();
//			return this;
//		}
//		
//		public PagedGuiBuilder element(ItemStack stack, ClickCallback callback) {
//			return element(new GuiElement(stack, Objects.requireNonNullElse(callback, GuiElement.EMPTY_CALLBACK)));
//		}
//		
//		public PagedGuiBuilder element(ItemStack stack) {
//			return element(stack, null);
//		}
//		
//		public PagedGuiBuilder skip() {
//			currentIndex++;
//			if (isRestricted(currentIndex)) skip();
//			return this;
//		}
//		
//		public PagedGuiBuilder skip(int i) {
//			currentIndex += i;
//			if (isRestricted(currentIndex)) skip();
//			return this;
//		}
//
//		public PagedGuiBuilder elements(List<GuiElementInterface> elements) {
//			if (elements!=null) elements.forEach(this::element);
//			return this;
//		}
//		
//		public PagedGuiBuilder nextPage() {
//			currentPage++;
//			currentIndex = 0;
//			createPage();
//			return this;
//		}
//		
//		private Int2ObjectMap<Element> createPage() {
//			var page = new Int2ObjectOpenHashMap<Element>();
//			pages.add(page);
//			int index = currentPage;
//			page.put(size-1, new Element(ElementHelper.getLast(pageSetter(()->pages.size()-1))));
//			page.put(size-8, new Element(ElementHelper.getPrevious(pageSetter(index-1), index-1)));
//			page.put(size-9, new Element(ElementHelper.getFirst(pageSetter(0))));
//
//			if (index>0) pages.get(index-1).put(size-2, new Element(ElementHelper.getNext(pageSetter(index), index)));
//			return page;
//		}
//		
//		private static ClickCallback pageSetter(IntSupplier index) {
//			return (i,t,a,g)->{if (g instanceof PagedGui p) p.setCurrentPage(index.getAsInt());};
//		}
//
//		private static ClickCallback pageSetter(int index) {
//			return (i,t,a,g)->{if (g instanceof PagedGui p) p.setCurrentPage(index);};
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
//		private boolean isRestricted(int i) {
//			return i==size-1 || i==size-2 || i==size-8 || i==size-9;
//		}
//	}
//}