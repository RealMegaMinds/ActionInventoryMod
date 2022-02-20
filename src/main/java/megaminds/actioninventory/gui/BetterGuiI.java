package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.util.Identifier;

public interface BetterGuiI extends SlotGuiInterface {
	/**
	 * Sets this gui's id. Can be null.
	 */
	void setId(Identifier id);
	/**
	 * Returns this gui's id. Can be null.
	 */
	Identifier getId();
	/**
	 * Returns the previous gui or null if there was no previous.
	 */
	BetterGuiI getPreviousGui();
	/**
	 * Sets whether the gui should open the previous gui on close.
	 */
	void setChained(boolean chain);
	/**
	 * Returns whether the gui should open the previous gui on close.
	 */
	boolean isChained();
	/**
	 * Opens this gui and sets the previous gui to the given one.
	 */
	boolean open(BetterGuiI previous);
	/**
	 * Should set previous to null.
	 */
	void clearPrevious();
	
	/**
	 * Resends the gui if it is already open, otherwise nothing.
	 * <br>
	 * Returns true is successful
	 */
	boolean reOpen();

	/**
	 * {@inheritDoc}
	 * <br>
	 * Reopens previous gui.
	 */
	@Override
	default void onClose() {
		BetterGuiI lastGui = getPreviousGui();
		if (isChained() && lastGui!=null) {
			clearPrevious();
			lastGui.reOpen();
			lastGui = null;
		}
	}
}