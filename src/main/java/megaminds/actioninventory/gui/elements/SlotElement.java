package megaminds.actioninventory.gui.elements;

import megaminds.actioninventory.gui.NamedGui;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.server.network.ServerPlayerEntity;

@Poly
public sealed interface SlotElement permits SlotFunction, AccessableElement {
	/**
	 * Use {@link NamedGui#setSlot}
	 */
	void apply(NamedGui gui, ServerPlayerEntity player);
	
	int getIndex();
	
	/**
	 * @throws IllegalArgumentException If {@code test} is negative and {@code gui} has no empty slots.
	 */
	default int getCheckedIndex(NamedGui gui) {
		int test = getIndex();
		if (test<0) test = gui.getFirstEmptySlot();
		if (test<0) throw new IllegalArgumentException("No more empty slots. Index must be specified.");
		return test;
	}
}