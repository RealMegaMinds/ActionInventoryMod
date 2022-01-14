package megaminds.actioninventory.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import eu.pb4.sgui.api.elements.GuiElementInterface.ItemClickCallback;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.actions.BasicAction;
import net.minecraft.item.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccessableGuiElement extends AccessableElement {
    protected ItemStack item;

    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    /**
     * Sets the display ItemStack
     *
     * @param itemStack the display item
     */
    public void setItemStack(ItemStack itemStack) {
        this.item = itemStack;
    }

    @Override
    public ClickCallback getGuiCallback() {
        return this.callback;
    }

    @Override
    public ItemStack getItemStackInternalUseOnly() {
        return this.item.copy();
    }
}