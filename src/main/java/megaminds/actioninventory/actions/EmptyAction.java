package megaminds.actioninventory.actions;

import eu.pb4.sgui.api.ClickType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.gui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.Instanced;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.screen.slot.SlotActionType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@PolyName("Empty")
@Instanced
public final class EmptyAction extends BasicAction {
	public static final EmptyAction INSTANCE = new EmptyAction();
	
	@Override
	public void validate() {
		//Unused
	}
	
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		//Unused
	}

	@Override
	public BasicAction copy() {
		return INSTANCE;
	}
}