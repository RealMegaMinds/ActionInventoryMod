package megaminds.actioninventory.gui;

import static megaminds.actioninventory.gui.SlotElement.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import com.google.gson.annotations.JsonAdapter;

import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.misc.ItemStackish;
import megaminds.actioninventory.misc.Validated;
import megaminds.actioninventory.serialization.PolymorphicTypeAdapterFactory;
import net.minecraft.item.ItemStack;

@JsonAdapter(PolymorphicTypeAdapterFactory.class)
public sealed abstract class SlotElement implements Validated permits Normal, Animated, Redirect {
	protected int index;
//	int index = integer(slot.get(INDEX), builder::getFirstEmptySlot);
//	if (index >= builder.getSize()) throw new JsonParseException("Slot index must be less than the defined size of: "+builder.getSize());
//	if (index < 0) throw new JsonParseException("No more empty slots. Slot index must be defined.");

	abstract void apply(NamedGuiBuilder builder);
	
	@Override
	public void validate() {
		//REMOVE
	}

	public static final class Normal extends SlotElement {
		private BasicAction[] actions;
		private ItemStackish display;
		
		public void apply(NamedGuiBuilder builder) {
			ItemStack s = display==null ? ItemStack.EMPTY : display.toStack();
			builder.setSlot(index, new AccessableGuiElement(s, actions));
		}
	}
	
	public static final class Animated extends SlotElement {
	    private BasicAction[] actions;
	    private ItemStackish[] items;
	    private int interval;
	    private boolean random;

		public void apply(NamedGuiBuilder builder) {
			ItemStack[] s = items==null ? new ItemStack[0] : Arrays.stream(items).filter(Objects::nonNull).map(ItemStackish::toStack).toArray(ItemStack[]::new);
			builder.setSlot(index, new AccessableAnimatedGuiElement(s, interval, random, actions));
//			b.setSlot(i, c.<AccessableAnimatedGuiElement>deserialize(s, AccessableAnimatedGuiElement.class));
		}
		
		@Override
		public void validate() {
			Validated.validate(interval>=0, "Animated element requires interval to be 0 or greater.");
		}
	}
	
	public static final class Redirect extends SlotElement {
		public void apply(NamedGuiBuilder builder) {
//			b.setSlot(i, c.<SlotFunction>deserialize(s, SlotFunction.class));
		}
	}
}