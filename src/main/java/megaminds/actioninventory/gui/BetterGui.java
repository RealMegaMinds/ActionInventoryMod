package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BetterGui extends SimpleGui implements BetterGuiI {
	private Identifier id;

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

	public Identifier getId() {
		return id;
	}

	public void setId(Identifier id) {
		this.id = id;
	}

	public BetterGuiI getPreviousGui() {
		return previousGui;
	}

	public boolean isChained() {
		return chained;
	}

	public void setChained(boolean chained) {
		this.chained = chained;
	}
}