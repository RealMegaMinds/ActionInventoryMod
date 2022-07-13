package megaminds.actioninventory.gui.elements;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.actions.ClickAwareAction;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.item.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@PolyName("Normal")
public final class AccessableGuiElement extends AccessableElement {
	private ItemStack item;

	public AccessableGuiElement(int index, ClickAwareAction action, ItemStack item) {
		super(index, action);
		this.item = item;
	}

	@NotNull
	@Override
	public ItemStack getItemStack() {
		return this.item.copy();
	}

	@Override
	public void validate(@NotNull Consumer<String> errorReporter) {
		super.validate(errorReporter);
		if (item==null) item = ItemStack.EMPTY;
	}

	@Override
	public SlotElement copy() {
		return new AccessableGuiElement(getIndex(), getGuiCallback(), item);
	}
}