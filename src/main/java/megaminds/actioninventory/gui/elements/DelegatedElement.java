package megaminds.actioninventory.gui.elements;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.GuiInterface;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.util.ElementHelper;
import net.minecraft.item.ItemStack;

public class DelegatedElement implements GuiElementInterface {
    private ClickCallback guiCallback;
    private GuiElementInterface delegate;
    
    public DelegatedElement(GuiElementInterface delegate, ClickCallback callback) {
    	this.delegate = delegate;
    	this.guiCallback = callback;
	}

	public ItemStack getItemStack() {
		return delegate.getItemStack();
	}

	public void appendCallback(ClickCallback callback) {
		this.guiCallback = ElementHelper.combine(this.guiCallback, callback);
	}

	@Override
	public ItemStack getItemStackForDisplay(GuiInterface gui) {
		return delegate.getItemStackForDisplay(gui);
	}

	@Override
	public void onAdded(SlotGuiInterface gui) {
		delegate.onAdded(gui);
	}

	@Override
	public void onRemoved(SlotGuiInterface gui) {
		delegate.onRemoved(gui);
	}

	@Override
	public ClickCallback getGuiCallback() {
		return guiCallback;
	}

	public void setGuiCallback(ClickCallback guiCallback) {
		this.guiCallback = guiCallback;
	}

	public GuiElementInterface getDelegate() {
		return delegate;
	}

	public void setDelegate(GuiElementInterface delegate) {
		this.delegate = delegate;
	}
}