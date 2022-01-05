package megaminds.actioninventory.gui;

import org.jetbrains.annotations.ApiStatus.Internal;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

public class NamedGui extends SimpleGui {
	/**
	 * Just a copy of the builder's name.
	 */
	private String name;
	
	public NamedGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, String name) {
		super(type, player, includePlayerInventorySlots);
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	@Internal
	public boolean click(int index, ClickType type, SlotActionType action) {
        GuiElementInterface element = this.getSlot(index);
        if (element != null) {
            element.getGuiCallback().click(index, type, action, this);
        }
        return this.onClick(index, type, action, element);
	}
	
	@FunctionalInterface
	public static interface NamedGuiCallback extends ClickCallback {
		@Override
		default void click(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
			if (gui instanceof NamedSlotGuiInterface) {
				click(index, type, action, (NamedSlotGuiInterface)gui);
			} else {
				throw new UnsupportedOperationException("SlotGuiInterface for a NamedGuiCallback must be an instance of NamedSlotGuiInterface!");
			}
		}
		void click(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui);
	}
	
	public static interface NamedSlotGuiInterface extends SlotGuiInterface {
		String getName();
	}
}