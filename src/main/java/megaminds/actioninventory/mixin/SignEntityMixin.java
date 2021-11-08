package megaminds.actioninventory.mixin;

import org.spongepowered.asm.mixin.Mixin;

import megaminds.actioninventory.SpecialSign;
import net.minecraft.block.entity.SignBlockEntity;

@Mixin(value = SignBlockEntity.class)
public class SignEntityMixin implements SpecialSign {
	private boolean isSpecialSign;
	private String name;

	@Override
	public boolean isSpecialSign() {
		return isSpecialSign;
	}

	@Override
	public void setSpecialSign(boolean isSpecial) {
		isSpecialSign = isSpecial;
	}

	@Override
	public String getSpecial() {
		return name;
	}

	@Override
	public void setSpecial(String name) {
		this.name = name;
		isSpecialSign = true;
	}
}