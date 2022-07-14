package megaminds.actioninventory.util;

import java.util.Map;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.gui.callback.CancellableCallback;
import megaminds.actioninventory.gui.elements.DelegatedElement;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralTextContent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElementHelper {
	public static final ClickCallback CLOSE = (i,t,a,g)->g.close();

	public static GuiElementInterface getLast(ClickCallback callback) {
		return of(Items.ARROW, "Last", 1, true, callback);
	}
	
	public static GuiElementInterface getFirst(ClickCallback callback) {
		return of(Items.ARROW, "First", 1, true, callback);
	}
	
	public static GuiElementInterface getNext(ClickCallback callback, int nextIndex) {
		return of(Items.ARROW, "Next", nextIndex+1, callback);
	}
	
	public static GuiElementInterface getPrevious(ClickCallback callback, int previousIndex) {
		return previousIndex<0 ? null : of(Items.ARROW, "Previous",  previousIndex+1, callback);
	}

	public static GuiElementInterface getCancel(ClickCallback callback) {
		return of(Items.RED_WOOL, "Cancel", 1, combine(callback, CLOSE));
	}

	public static GuiElementInterface getConfirm(ClickCallback callback) {
		return getDone(callback, "Confirm");
	}
	
	public static GuiElementInterface getDone(ClickCallback callback, String name) {
		return of(Items.GREEN_WOOL, name, 1, combine(callback, CLOSE));
	}

	public static GuiElementInterface of(Item item, String name, int count, ClickCallback callback) {
		return of(item, name, count, false, callback);
	}

	public static GuiElementInterface of(Item item, String name, int count, boolean glint, ClickCallback callback) {
		ItemStack stack = new ItemStack(item, count);
		stack.setCustomName(new LiteralTextContent(name));
		if (glint) addGlint(stack);
		return new GuiElement(hideAllFlags(stack), callback);
	}

	public static GuiElementInterface getDelegate(GuiElementInterface el, ClickCallback callback, boolean appendCallback) {
		if (el instanceof DelegatedElement e) {
			return e;
		}
		return new DelegatedElement(el, appendCallback ? combine(el.getGuiCallback(), callback) : callback);
	}

	public static ItemStack addGlint(ItemStack stack) {
		if (!stack.hasGlint()) {
			stack.addEnchantment(null, 0);
		}
		return stack;
	}

	public static ItemStack removeEnchants(ItemStack stack) {
		EnchantmentHelper.set(Map.of(), stack);
		return stack;
	}

	public static ItemStack hideAllFlags(ItemStack stack) {
		for (TooltipSection f : TooltipSection.values()) {
			stack.addHideFlag(f);
		}
		return stack;
	}

	public static ClickCallback combine(ClickCallback one, ClickCallback two) {
		if (one!=null && two==null) return one;
		if (two!=null && one==null) return two;
		if (one==null && one==null) return GuiElement.EMPTY_CALLBACK;
		if (one instanceof CancellableCallback c) {
			return (CancellableCallback)(i,t,a,g) -> {
				if (!c.cancellingClick(i,t,a,g)) {
					two.click(i, t, a, g);
					return false;
				}
				return true;
			};
		}
		return (i,t,a,g) -> {
			one.click(i, t, a, g);
			two.click(i, t, a, g);
		};
	}
}