package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.GuiInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.misc.Validated;
import megaminds.actioninventory.util.annotations.Exclude;
import net.minecraft.item.ItemStack;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccessableAnimatedGuiElement extends AccessableElement {
	private ItemStack[] items;
	private int interval;
	private boolean random;
	@Exclude private int frame = 0;
	@Exclude private int tick = 0;

	public AccessableAnimatedGuiElement(BasicAction action, ItemStack[] items, int interval, boolean random) {
		this.action = action;
		this.items = items;
		this.interval = interval;
		this.random = random;
	}

	@Override
	public void validate() {
		super.validate();
		Validated.validate(interval>=0, "Animated gui element requires interval to be 0 or greater.");
		if (items==null) items = new ItemStack[]{ItemStack.EMPTY};
	}
	
	@Override
	public ItemStack getItemStack() {
		return this.items[frame];
	}

	@Override
	public ItemStack getItemStackForDisplay(GuiInterface gui) {
		int cFrame = frame;

		tick += 1;
		if (tick >= interval) {
			tick = 0;
			if (this.random) {
				this.frame = (int) (Math.random() * this.items.length);
			} else {
				frame += 1;
				if (frame >= items.length) frame = 0;
			}
		}


		return this.items[cFrame].copy();
	}
}