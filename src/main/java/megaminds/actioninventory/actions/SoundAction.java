package megaminds.actioninventory.actions;

import java.util.Objects;

import eu.pb4.sgui.api.ClickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.NamedSlotGuiInterface;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * This plays a sound
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PolyName("Sound")
public final class SoundAction extends BasicAction {
	private SoundEvent sound;
	private SoundCategory category;
	private Vec3d position;
	private Float volume;
	private Float pitch;
	
	public SoundAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Identifier requiredGuiName, SoundEvent sound, SoundCategory category, Vec3d position, Float volume, Float pitch) {
		super(requiredIndex, clicktype, actionType, requiredGuiName);
		this.sound = sound;
		this.category = category;
		this.position = position;
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		
		player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(sound.getId(), category, Objects.requireNonNullElseGet(position, player::getPos), volume, pitch));
	}

	@Override
	public void validate() {
		if (sound==null) sound = SoundEvents.UI_BUTTON_CLICK;
		if (category==null) category = SoundCategory.MASTER;
		if (volume==null) volume = 1f;
		if (pitch==null) pitch = 1f;
		Validated.validate(volume>=0, "Sound action requires volume to be 0 or above, but it is: "+volume);
		Validated.validate(pitch>=0, "Sound action requires pitch to be 0 or above, but it is: "+pitch);
	}
}