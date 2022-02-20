package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@Getter
@Setter
public class BetterGui extends SimpleGui implements BetterGuiI {
	private Identifier id;

	@Setter(AccessLevel.NONE)
	/**
	 * Note, this is set on open.
	 */
	private BetterGuiI previousGui;
	private boolean chained;

	public BetterGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots) {
		super(type, player, includePlayerInventorySlots);
	}
	
	@Override
	public boolean open(BetterGuiI previous) {
		this.previousGui = previous;
		return open();
	}

	@Override
	public void clearPrevious() {
		previousGui = null;
	}

	@Override
	public boolean reOpen() {
		if (isOpen()) sendGui();
		return false;
	}
}