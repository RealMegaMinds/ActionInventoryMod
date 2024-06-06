package megaminds.actioninventory.util;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.gui.NavigableGui;
import megaminds.actioninventory.gui.NavigableGui.NavigableCallback;
import megaminds.actioninventory.gui.callback.CancellableCallback;
import megaminds.actioninventory.gui.elements.HideableElement;
import megaminds.actioninventory.gui.input.InputCallback;
import megaminds.actioninventory.gui.input.InputSplitter;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElementHelper {
	public static final ClickCallback CLOSE = (i,t,a,g)->g.close();

	public static final NavigableCallback FIRST		= (i,t,a,g)->{g.goToFirst();;return false;};
	public static final NavigableCallback PREVIOUS	= (i,t,a,g)->{g.goToPrevious();;return false;};
	public static final NavigableCallback NEXT		= (i,t,a,g)->{g.goToNext();; return false;};
	public static final NavigableCallback LAST		= (i,t,a,g)->{g.goToLast();; return false;};

	public static GuiElementInterface getLast() {
		return new HideableElement(stackOf(Items.ARROW, 1, "Last", true), LAST) {
			@Override
			public void onAdded(SlotGuiInterface gui) {
				if (gui instanceof NavigableGui lg) {
					lg.addCallback(g->{
						if (g.getCurrent()!=g.getCount()-1) {
							this.setHidden(false);
						} else {
							this.setHidden(true);
						}
					});
				}
			}
		};
	}

	public static GuiElementInterface getFirst() {
		return new HideableElement(stackOf(Items.ARROW, 1, "First", true), FIRST) {
			@Override
			public void onAdded(SlotGuiInterface gui) {
				if (gui instanceof NavigableGui lg) {
					lg.addCallback(g->{
						if (g.getCurrent()!=0) {
							this.setHidden(false);
						} else {
							this.setHidden(true);
						}
					});
				}
			}
		};
	}

	public static GuiElement getNext() {
		return new HideableElement(stackOf(Items.ARROW, "Next"), NEXT) {
			@Override
			public void onAdded(SlotGuiInterface gui) {
				if (gui instanceof NavigableGui lg) {
					lg.addCallback(g->{
						this.item.setCount(g.getCurrent()+2);
						if (g.getCurrent()!=g.getCount()-1) {
							this.setHidden(false);
						} else {
							this.setHidden(true);
						}
					});
				}
			}
		};
	}

	public static GuiElement getPrevious() {
		return new HideableElement(stackOf(Items.ARROW, "Previous"), PREVIOUS) {
			@Override
			public void onAdded(SlotGuiInterface gui) {
				if (gui instanceof NavigableGui lg) {
					lg.addCallback(g->{
						this.item.setCount(g.getCurrent());
						if (g.getCurrent()!=0) {
							this.setHidden(false);
						} else {
							this.setHidden(true);
						}
					});
				}
			}
		};
	}

	public static GuiElementInterface getCancel() {
		return of(Items.RED_WOOL, "Cancel", 1, CLOSE);
	}

	public static GuiElementInterface getConfirm(ClickCallback callback) {
		return getDone(callback, "Confirm");
	}

	public static GuiElementInterface getDone(ClickCallback callback, String name) {
		return of(Items.GREEN_WOOL, name, 1, combine(CLOSE, callback));
	}

	public static <E> GuiElementInterface getSelectable(E e, Function<E, ItemStack> func, BiFunction<SlotGuiInterface, E, Boolean> select, BiFunction<SlotGuiInterface, E, Boolean> deselect, boolean isSelected) {
		if (isSelected) {
			return new GuiElement(addGlint(func.apply(e)), (i,t,a,g)->{
				if (deselect.apply(g, e)) {
					g.setSlot(i, getSelectable(e, func, select, deselect, false));
				}
			});
		} else {
			return new GuiElement(removeEnchants(func.apply(e)), (i,t,a,g)->{
				if (select.apply(g, e)) {
					g.setSlot(i, getSelectable(e, func, select, deselect, true));
				}
			});
		}
	}

	public static <E> GuiElementInterface getEditable(E e, Function<E, ItemStack> func, Consumer<E> onRemove, BiConsumer<E, E> onEdit, InputCallback<E> editor) {
		return new GuiElement(func.apply(e), new InputSplitter()
				.question(new LiteralText("Edit or Remove Item"))
				.choices(Map.of(editor.copy().defaultAnswer(e).onFinish(n->onEdit.accept(e, n)),
						ElementHelper.stackOf(Items.WRITABLE_BOOK, "Edit"),
						(i,t,a,g)->onRemove.accept(e),
						ElementHelper.stackOf(Items.BARRIER, "Remove"))));
	}

	public static GuiElementInterface of(Item item, String name, int count, ClickCallback callback) {
		return of(item, name, count, false, callback);
	}

	public static GuiElementInterface of(Item item, String name, int count, boolean glint, ClickCallback callback) {
		return new GuiElement(stackOf(item, count, name, glint), callback);
	}

	public static GuiElementInterface of(Item item, String name, int count, boolean glint, boolean addClose, ClickCallback... callbacks) {
		return of(item, name, count, glint, addClose ? combine(CLOSE, callbacks) : combine(null, callbacks));
	}

	public static ItemStack stackOf(Item item, int count, Text name, boolean glint) {
		ItemStack stack = new ItemStack(item, count);
		if (name!=null) stack.setCustomName(name);
		if (glint) addGlint(stack);
		return hideAllFlags(stack);		
	}

	public static ItemStack stackOf(Item item, int count, String name, boolean glint) {
		return stackOf(item, count, name==null?null:new LiteralText(name), glint);
	}

	public static ItemStack stackOf(Item item, String name) {
		return stackOf(item, 1, name, false);
	}

	public static ItemStack stackOf(Item item, Text name) {
		return stackOf(item, 1, name, false);
	}

	public static ItemStack stackOf(Item item, String name, boolean glint) {
		return stackOf(item, 1, name, glint);
	}

	public static ItemStack addGlint(ItemStack stack) {
		if (!stack.hasGlint()) {
			stack.addEnchantment(null, 0);
		}
		return stack;
	}

	public static ItemStack addLore(ItemStack stack, Text lore) {
		if (lore==null) return stack;
		var nbt = stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY);
		var list = Objects.requireNonNullElseGet(nbt.getList(ItemStack.LORE_KEY, NbtType.STRING), NbtList::new);
		list.add(NbtString.of(Text.Serializer.toJson(lore)));
		nbt.put(ItemStack.LORE_KEY, list);
		return stack;
	}

	public static ItemStack setLore(ItemStack stack, Text lore) {
		if (lore==null) return removeLore(stack);
		var list = new NbtList();
		list.add(NbtString.of(Text.Serializer.toJson(lore)));
		stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, list);
		return stack;
	}

	public static ItemStack removeLore(ItemStack stack) {
		var nbt = stack.getSubNbt(ItemStack.DISPLAY_KEY);
		if (nbt!=null) nbt.remove(ItemStack.LORE_KEY);
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

	public static ClickCallback combine(ClickCallback last, ClickCallback... callbacks) {
		CancellableCallback callback = CancellableCallback.EMPTY;
		if (callbacks!=null) {
			for (ClickCallback c : callbacks) {
				callback = callback.andThen(c);
			}
		}
		return callback.andThen(last);
	}
}