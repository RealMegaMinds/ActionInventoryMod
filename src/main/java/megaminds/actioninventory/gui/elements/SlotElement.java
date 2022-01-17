package megaminds.actioninventory.gui.elements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.gui.NamedGui;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.server.network.ServerPlayerEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Poly
public abstract sealed class SlotElement permits SlotFunction, AccessableElement {
	private int index;
	
	/**
	 * Use {@link NamedGui#setSlot}
	 */
	public abstract void apply(NamedGui gui, ServerPlayerEntity player);
	
	public abstract SlotElement copy();
		
	/**
	 * @throws IllegalArgumentException If {@code test} is negative and {@code gui} has no empty slots.
	 */
	public int getCheckedIndex(NamedGui gui) {
		int test = getIndex();
		if (test<0) test = gui.getFirstEmptySlot();
		if (test<0) throw new IllegalArgumentException("No more empty slots. Index must be specified.");
		return test;
	}
}