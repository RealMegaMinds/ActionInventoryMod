package megaminds.testmod.mixin;

import org.spongepowered.asm.mixin.Mixin;

import megaminds.testmod.ClickChecker;
import net.minecraft.inventory.Inventory;

@Mixin(Inventory.class)
public interface InventoryMixin extends ClickChecker {
}