package megaminds.actioninventory.actions;

import java.util.Objects;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.TypeName;
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
	private SoundEvent sound = SoundEvents.UI_BUTTON_CLICK;
	private SoundCategory category = SoundCategory.MASTER;
	private Vec3d position;
	private float volume = 1f;
	private float pitch = 1f;

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(sound.getId(), category, Objects.requireNonNullElseGet(position, player::getPos), volume, pitch));
	}
}