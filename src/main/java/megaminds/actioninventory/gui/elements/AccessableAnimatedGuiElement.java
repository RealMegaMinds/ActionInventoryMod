package megaminds.actioninventory.gui.elements;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.gui.GuiInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Exclude;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.item.ItemStack;

/**
 * Adapted from {@link eu.pb4.sgui.api.elements.AnimatedGuiElement}
 */
@Getter
@Setter
@NoArgsConstructor
@PolyName("Animated")
public final class AccessableAnimatedGuiElement extends AccessableElement {
	private static final ItemStack[] EMPTY = {ItemStack.EMPTY};
	
	private ItemStack[] items;
	private int interval;
	private boolean random;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Exclude private int frame = 0;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Exclude private int tick = 0;

	public AccessableAnimatedGuiElement(int index, BasicAction action, ItemStack[] items, int interval, boolean random) {
		super(index, action);
		this.items = items;
		this.interval = interval;
		this.random = random;
	}

	@Override
	public void validate() {
		super.validate();
		Validated.validate(interval>=0, "Animated gui element requires interval to be 0 or greater.");
		if (items==null) items = EMPTY;
	}
	
	@NotNull
	@Override
	public ItemStack getItemStack() {
		return this.items[frame].copy();
	}

	@Override
	public ItemStack getItemStackForDisplay(GuiInterface gui) {
		if (items.length==1) return items[0];
		
		int cFrame = frame;

		tick += 1;
		if (tick >= interval) {
			tick = 0;
			if (this.random) {
				this.frame = ActionInventoryMod.RANDOM.nextInt(items.length);
			} else {
				frame += 1;
				if (frame >= items.length) frame = 0;
			}
		}

		return this.items[cFrame].copy();
	}

	@Override
	public SlotElement copy() {
		AccessableAnimatedGuiElement copy = new AccessableAnimatedGuiElement();
		copy.setAction(getGuiCallback().copy());
		copy.items = Arrays.stream(items).map(ItemStack::copy).toArray(ItemStack[]::new);
		copy.interval = interval;
		copy.random = random;
		return copy;
	}
}