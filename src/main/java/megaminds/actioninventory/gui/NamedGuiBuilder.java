package megaminds.actioninventory.gui;

import com.google.gson.annotations.JsonAdapter;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.serialization.NamedGuiBuilderSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Originally cloned from {@link eu.pb4.sgui.api.gui.SimpleGuiBuilder} and then many changes made.
 */
@JsonAdapter(NamedGuiBuilderSerializer.class)
public final class NamedGuiBuilder {
	private final int size;
	private final ScreenHandlerType<?> type;
	private final GuiElementInterface[] elements;
	private final SlotFunction[] slotRedirects;
	private final boolean includePlayer;
	private boolean lockPlayerInventory, hasRedirects;
	private Text title;
	private String name;

	public NamedGuiBuilder(ScreenHandlerType<?> type, boolean includePlayerInventorySlots) {
		this.type = type;

		this.size = GuiHelpers.getHeight(type)*GuiHelpers.getWidth(type) + (includePlayerInventorySlots ? 36 : 0);
		this.elements = new GuiElementInterface[this.size];
		this.slotRedirects = new SlotFunction[this.size];

		this.includePlayer = includePlayerInventorySlots;
	}

	public NamedGui build(ServerPlayerEntity player) {
		NamedGui gui = new NamedGui(this.type, player, this.includePlayer, this.name);
		gui.setTitle(this.title);
		gui.setLockPlayerInventory(true);

		int pos = 0;

		for (GuiElementInterface element : this.elements) {
			if (element != null) {
				gui.setSlot(pos, element);
			}
			pos++;
		}

		pos = 0;

		for (SlotFunction slot : this.slotRedirects) {
			if (slot != null) {
				gui.setSlotRedirect(pos, slot.apply(player));
			}
			pos++;
		}

		return gui;
	}

	public void setSlot(int index, AccessableGuiElement element) {
		this.elements[index] = element;
	}

	public void setSlot(int index, AccessableAnimatedGuiElement element) {
		this.elements[index] = element;
	}

	public void addSlot(AccessableGuiElement element) {
		this.setSlot(this.getFirstEmptySlot(), element);
	}

	public void addSlot(AccessableAnimatedGuiElement element) {
		this.setSlot(this.getFirstEmptySlot(), element);
	}

	public void setSlot(int index, ItemStack itemStack) {
		this.setSlot(index, new AccessableGuiElement(itemStack));
	}

	public void addSlot(ItemStack itemStack) {
		this.setSlot(this.getFirstEmptySlot(), itemStack);
	}

	public void setSlot(int index, ItemStack itemStack, BasicAction callback) {
		this.setSlot(index, new AccessableGuiElement(itemStack, callback));
	}

	public void addSlot(ItemStack itemStack, BasicAction callback) {
		this.setSlot(this.getFirstEmptySlot(), new AccessableGuiElement(itemStack, callback));
	}

	public int getFirstEmptySlot() {
		for (int i = 0; i < this.elements.length; i++) {
			if (this.elements[i] == null && this.slotRedirects[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public boolean isIncludingPlayer() {
		return this.includePlayer;
	}

	public GuiElementInterface getSlot(int index) {
		if (index >= 0 && index < this.size) {
			return this.elements[index];
		}
		return null;
	}

	public boolean isRedirectingSlots() {
		return this.hasRedirects;
	}

	public Text getTitle() {
		return this.title;
	}

	public void setTitle(Text title) {
		this.title = title;
	}

	public ScreenHandlerType<?> getType() {
		return this.type;
	}

	public int getSize() {
		return this.size;
	}

	public boolean getLockPlayerInventory() {
		return this.lockPlayerInventory || this.includePlayer;
	}

	public void setLockPlayerInventory(boolean value) {
		this.lockPlayerInventory = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSlot(int index, SlotFunction slot) {
		this.elements[index] = null;
		this.slotRedirects[index] = slot;
		this.hasRedirects = true;
	}

	public void addSlot(SlotFunction slot) {
		this.setSlot(this.getFirstEmptySlot(), slot);
	}

	public SlotFunction getSlotFunc(int index) {
		if (index >= 0 && index < this.size) {
			return this.slotRedirects[index];
		}
		return null;
	}
}