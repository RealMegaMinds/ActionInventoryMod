package megaminds.actioninventory.gui.elements;

import java.util.Arrays;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.sgui.api.gui.GuiInterface;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.annotations.Exclude;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.item.ItemStack;

/**
 * Adapted from {@link eu.pb4.sgui.api.elements.AnimatedGuiElement}
 */
@PolyName("Animated")
public final class AccessableAnimatedGuiElement extends AccessableElement {
	private static final ItemStack[] EMPTY = {ItemStack.EMPTY};

	private ItemStack[] items;
	private int interval;
	private TriState random = TriState.DEFAULT;

	@Exclude private int frame;
	@Exclude private int tick;

	public AccessableAnimatedGuiElement() {}

	public AccessableAnimatedGuiElement(int index, BasicAction action, ItemStack[] items, int interval, TriState random) {
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

	@Override
	public ItemStack getItemStackForDisplay(GuiInterface gui) {
		if (items.length==1) return items[0];

		var cFrame = frame;

		tick += 1;
		if (tick >= interval) {
			tick = 0;
			if (random.orElse(false)) {
				this.frame = ActionInventoryMod.RANDOM.nextInt(items.length);
			} else {
				frame += 1;
				if (frame >= items.length) frame = 0;
			}
		}

		var stack = Helper.parseItemStack(this.items[cFrame].copy(), PlaceholderContext.of(gui.getPlayer()));
		lastDisplayed = stack;
		return stack;
	}

	@Override
	public SlotElement copy() {
		var copy = new AccessableAnimatedGuiElement();
		copy.setAction(getGuiCallback().copy());
		copy.items = Arrays.stream(items).map(ItemStack::copy).toArray(ItemStack[]::new);
		copy.interval = interval;
		copy.random = random;
		return copy;
	}

	public ItemStack[] getItems() {
		return items;
	}

	public void setItems(ItemStack[] items) {
		this.items = items;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public TriState getRandom() {
		return random;
	}

	public void setRandom(TriState random) {
		this.random = random;
	}
}