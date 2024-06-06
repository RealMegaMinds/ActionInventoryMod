package megaminds.actioninventory.gui;

import java.util.OptionalInt;

import org.jetbrains.annotations.Nullable;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilderInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import eu.pb4.sgui.api.elements.GuiElementInterface.ItemClickCallback;
import eu.pb4.sgui.virtual.inventory.VirtualScreenHandler;
import eu.pb4.sgui.virtual.inventory.VirtualScreenHandlerFactory;
import eu.pb4.sgui.virtual.inventory.VirtualSlot;
import megaminds.actioninventory.gui.elements.Element;
import megaminds.actioninventory.util.Helper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class BaseGui implements BetterGuiI {
	private final ScreenHandlerType<?> type;
	private final ServerPlayerEntity player;
	private final int height;
	private final int width;
	private final int size;
	private final int virtualSize;

	private BetterGuiI previousGui;
	private boolean chained;
	private Identifier id;

	private boolean includingPlayer;
	private boolean lockPlayerInventory;
	private boolean autoUpdate = true;
	private boolean redirectingSlots;
	private Text title;

	private boolean open;
	private int syncId = -1;
	private VirtualScreenHandler screenHandler;
	private boolean reOpen;

	public BaseGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayer) {
		this.type = type;
		this.player = player;
		this.includingPlayer = includePlayer;
		this.height = GuiHelpers.getHeight(type);
		this.width = GuiHelpers.getWidth(type);
		this.virtualSize = width * height;
		this.size = virtualSize + (includePlayer ? PlayerInventory.MAIN_SIZE : 0);
	}

	protected void setRedirectingSlots() {
		redirectingSlots = true;
	}

	public boolean inBounds(int index) {
		return index>=0 && index<size;
	}

	@Override
	public boolean open() {
		if (this.player.isDisconnected() || this.open) {
			return false;
		} else {
			this.open = true;
			this.onOpen();
			return this.sendGui();
		}
	}

	@Override
	public void close(boolean alreadyClosed) {
		if (this.open && !this.reOpen) {
			this.open = false;
			this.reOpen = false;

			if (!alreadyClosed && this.player.currentScreenHandler == this.screenHandler) {
				this.player.closeHandledScreen();
			}

			GuiHelpers.sendPlayerInventory(this.getPlayer());
			this.onClose();
		} else {
			this.reOpen = false;
		}
	}

	@Override
	public boolean open(BetterGuiI previous) {
		this.previousGui = previous;
		return open();
	}

	@Override
	public boolean reOpen() {
		if (isOpen()) sendGui();
		return false;
	}

	protected boolean sendGui() {
		this.reOpen = true;
		OptionalInt temp = this.player.openHandledScreen(new VirtualScreenHandlerFactory(this));
		this.reOpen = false;
		if (temp.isPresent()) {
			this.syncId = temp.getAsInt();
			if (this.player.currentScreenHandler instanceof VirtualScreenHandler) {
				this.screenHandler = (VirtualScreenHandler) this.player.currentScreenHandler;
				return true;
			}
		}
		return false;
	}

	protected void updateSlot(int index) {
		if (this.open && this.autoUpdate && this.screenHandler != null) {
			this.screenHandler.setSlot(index, new VirtualSlot(this.screenHandler.inventory, index, 0, 0));
		}
	}

	@Override
	public int getFirstEmptySlot() {
		for (int i = 0, l = getSize(); i < l; i++) {
			if (Element.isEmpty(getElement(i))) return i;
		}
		return -1;
	}

	@Override
	public void clearSlot(int index) {
		setElement(index, null);
	}

	public abstract Element getElement(int index);
	public abstract void addElement(Element element);
	public abstract void setElement(int index, Element element);

	@Override public GuiElementInterface getSlot(int index) {return Helper.apply(getElement(index), Element::getElement);}
	@Override public Slot getSlotRedirect(int index) {return Helper.apply(getElement(index), Element::getRedirect);}

	@Override public void addSlot(GuiElementBuilderInterface<?> element) {addSlot(element.build());}
	@Override public void addSlot(ItemStack itemStack) {addSlot(itemStack, GuiElement.EMPTY_CALLBACK);}
	@Override public void addSlot(ItemStack itemStack, ClickCallback callback) {addSlot(new GuiElement(itemStack, callback));}
	@Override public void addSlot(ItemStack itemStack, ItemClickCallback callback) {addSlot(new GuiElement(itemStack, callback));}
	@Override public void addSlot(GuiElementInterface element) {addElement(Element.create(element));}
	@Override public void addSlotRedirect(Slot slot) {addElement(Element.create(slot));}

	@Override public void setSlotRedirect(int index, Slot slot) {setElement(index, Element.create(slot));}
	@Override public void setSlot(int index, GuiElementInterface element) {setElement(index, Element.create(element));}

	@Override public int getSize() {return size;}
	@Override public boolean getLockPlayerInventory() {return lockPlayerInventory;}
	@Override public void setLockPlayerInventory(boolean value) {lockPlayerInventory = value || isIncludingPlayer();}
	@Override public int getHeight() {return height;}
	@Override public int getWidth() {return width;}
	@Override public boolean isRedirectingSlots() {return redirectingSlots;}
	@Override public void setTitle(Text title) {this.title = title;}
	@Override public @Nullable Text getTitle() {return title;}
	@Override public ScreenHandlerType<?> getType() {return type;}
	@Override public ServerPlayerEntity getPlayer() {return player;}
	@Override public int getSyncId() {return syncId;}
	@Override public boolean isOpen() {return open;}
	@Override public boolean getAutoUpdate() {return autoUpdate;}
	@Override public void setAutoUpdate(boolean value) {autoUpdate = value;}
	@Override public void setId(Identifier id) {this.id = id;}
	@Override public Identifier getId() {return id;}
	@Override public BetterGuiI getPreviousGui() {return previousGui;}
	@Override public void setChained(boolean chain) {this.chained = chain;}
	@Override public boolean isChained() {return chained;}
	@Override public void clearPrevious() {previousGui = null;}
	@Override public boolean isIncludingPlayer() {return includingPlayer;}
	@Override public int getVirtualSize() {return virtualSize;}
}