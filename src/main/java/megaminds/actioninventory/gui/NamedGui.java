package megaminds.actioninventory.gui;

import org.jetbrains.annotations.ApiStatus.Internal;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.Getter;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@Getter
public class NamedGui extends SimpleGui implements NamedSlotGuiInterface {
	/**
	 * Just a copy of the builder's name.
	 */
	private final Identifier name;
	
	public NamedGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, Identifier name) {
		super(type, player, includePlayerInventorySlots);
		this.name = name;
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
}