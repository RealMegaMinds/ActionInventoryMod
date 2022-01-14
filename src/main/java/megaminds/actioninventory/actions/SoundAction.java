package megaminds.actioninventory.actions;

import java.util.Objects;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.misc.Validated;
import megaminds.actioninventory.util.annotations.TypeName;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

/**
 * This plays a sound
 */
@TypeName("Sound")
public final class SoundAction extends BasicAction {
	private SoundEvent sound;
	private SoundCategory category;
	private Vec3d position;
	private float volume;
	private float pitch;

	private SoundAction() {}
	
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		
		player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(sound.getId(), category, Objects.requireNonNullElseGet(position, player::getPos), volume, pitch));
	}

	@Override
	public void validate() {
		if (sound==null) sound = SoundEvents.UI_BUTTON_CLICK;
		if (category==null) category = SoundCategory.MASTER;
		Validated.validate(volume>=0, "Sound action requires volume to be 0 or above, but it is: "+volume);
		Validated.validate(pitch>=0, "Sound action requires pitch to be 0 or above, but it is: "+pitch);
	}
}