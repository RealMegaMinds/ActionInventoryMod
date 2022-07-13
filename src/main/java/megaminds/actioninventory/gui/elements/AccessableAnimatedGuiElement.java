package megaminds.actioninventory.gui.elements;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.gui.GuiInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.actions.ClickAwareAction;
import megaminds.actioninventory.util.annotations.Exclude;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.item.ItemStack;

/**
 * Adapted from {@link eu.pb4.sgui.api.elements.AnimatedGuiElement}
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@PolyName("Animated")
public final class AccessableAnimatedGuiElement extends AccessableElement {
	private List<ItemStack> items;
	private int interval;
	private TriState random = TriState.DEFAULT;

	@Getter(AccessLevel.NONE)
	@Exclude private int frame;
	@Getter(AccessLevel.NONE)
	@Exclude private int tick;

	public AccessableAnimatedGuiElement(int index, ClickAwareAction action, List<ItemStack> items, int interval, TriState random) {
		super(index, action);
		this.items = items;
		this.interval = interval;
		this.random = random;
	}

	@Override
	public void validate(@NotNull Consumer<String> errorReporter) {
		super.validate(errorReporter);
		if (interval<0) errorReporter.accept("Interval is: "+interval+", but must be >= 0.");
		if (items==null) items = Collections.singletonList(ItemStack.EMPTY);
	}

	@NotNull
	@Override
	public ItemStack getItemStack() {
		return this.items.get(frame).copy();
	}

	@Override
	public ItemStack getItemStackForDisplay(GuiInterface gui) {
		var size = items.size();
		if (size==1) return items.get(0);

		var cFrame = frame;

		tick += 1;
		if (tick >= interval) {
			tick = 0;
			if (random.orElse(false)) {
				this.frame = ActionInventoryMod.RANDOM.nextInt(size);
			} else {
				frame += 1;
				if (frame >= size) frame = 0;
			}
		}

		return this.items.get(cFrame).copy();
	}

	@Override
	public SlotElement copy() {
		return new AccessableAnimatedGuiElement(frame, getGuiCallback(), items, interval, random);
	}
}