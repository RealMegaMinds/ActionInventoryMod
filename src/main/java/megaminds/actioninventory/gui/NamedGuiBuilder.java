package megaminds.actioninventory.gui;

import java.util.Arrays;

import com.google.gson.annotations.JsonAdapter;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.misc.Validated;
import megaminds.actioninventory.serialization.NamedGuiBuilderSerializer;
import megaminds.actioninventory.util.annotations.Exclude;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Originally cloned from {@link eu.pb4.sgui.api.gui.SimpleGuiBuilder} and then many changes made.
 */
@JsonAdapter(NamedGuiBuilderSerializer.class)
public final class NamedGuiBuilder implements Validated {
	private ScreenHandlerType<?> type;
	private Text title;
	private Identifier name;
	private boolean includePlayer;
	private boolean lockPlayerInventory;
	private SlotElement[] elements;
	
	@Exclude private GuiElementInterface[] guiElements;
	@Exclude private SlotFunction[] slotRedirects;
	@Exclude private boolean hasRedirects;
	@Exclude private int size;
	
	@Override
	public void validate() {
		Validated.validate(type!=null, "NamedGuiBuilder requires type to be non-null.");
		Validated.validate(name!=null, "NamedGuiBuilder requires name to be non-null.");
		if (title==null) title = LiteralText.EMPTY;
		this.size = GuiHelpers.getHeight(type)*GuiHelpers.getWidth(type) + (includePlayer ? 36 : 0);
		if (guiElements==null) {
			guiElements = new GuiElementInterface[size];
		} else {
			guiElements = Arrays.copyOf(guiElements, size);
		}
		if (slotRedirects==null) slotRedirects = new SlotFunction[size];
	}
	
	public NamedGuiBuilder() {}

	public NamedGuiBuilder(ScreenHandlerType<?> type, boolean includePlayerInventorySlots) {
		this.type = type;

		this.guiElements = new GuiElementInterface[this.size];
		this.slotRedirects = new SlotFunction[this.size];

		this.includePlayer = includePlayerInventorySlots;
	}

	public NamedGui build(ServerPlayerEntity player) {
		NamedGui gui = new NamedGui(this.type, player, this.includePlayer, this.name);
		gui.setTitle(this.title);
		gui.setLockPlayerInventory(true);

		int pos = 0;

		for (GuiElementInterface element : this.guiElements) {
			if (element != null) {
				gui.setSlot(pos, element);
			}
			pos++;
		}

		pos = 0;

		for (SlotFunction slot : this.slotRedirects) {
			if (slot != null) {
				gui.setSlotRedirect(pos, slot.getSlot(player));
			}
			pos++;
		}

		return gui;
	}

	public void setSlot(int index, AccessableGuiElement element) {
		this.guiElements[index] = element;
	}

	public void setSlot(int index, AccessableAnimatedGuiElement element) {
		this.guiElements[index] = element;
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
		for (int i = 0; i < this.guiElements.length; i++) {
			if (this.guiElements[i] == null && this.slotRedirects[i] == null) {
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
			return this.guiElements[index];
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

	public Identifier getName() {
		return name;
	}

	public void setName(Identifier name) {
		this.name = name;
	}

	public void setSlot(int index, SlotFunction slot) {
		this.guiElements[index] = null;
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