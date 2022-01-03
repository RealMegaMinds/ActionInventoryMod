package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.ScreenHandlerType;
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
}