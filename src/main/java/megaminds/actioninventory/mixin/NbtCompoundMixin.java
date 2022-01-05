package megaminds.actioninventory.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

@Mixin(NbtCompound.class)
public interface NbtCompoundMixin {
	@Invoker Map<String, NbtElement> invokeToMap();
	@Invoker static NbtCompound createNbtCompound(Map<String, NbtElement> map) {
		throw new AssertionError("NbtCompound Mixin Failed");
	}
}