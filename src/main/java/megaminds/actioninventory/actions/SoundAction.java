package megaminds.actioninventory.actions;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * This plays a sound
 */
@PolyName("Sound")
public final class SoundAction extends BasicAction {
	private SoundEvent sound;
	private SoundCategory category;
	private Vec3d position;
	private Float volume;
	private Float pitch;

	public SoundAction() {}

	public SoundAction(SoundEvent sound, SoundCategory category, Vec3d position, Float volume, Float pitch) {
		this.sound = sound;
		this.category = category;
		this.position = position;
		this.volume = volume;
		this.pitch = pitch;
	}

	@SuppressWarnings("java:S107")
	public SoundAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, SoundEvent sound, SoundCategory category, Vec3d position, Float volume, Float pitch) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
		this.sound = sound;
		this.category = category;
		this.position = position;
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void accept(@NotNull ActionInventoryGui gui) {
		var player = gui.getPlayer();
		var pos = Objects.requireNonNullElseGet(position, player::getPos);
		player.networkHandler.sendPacket(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(sound), category, pos.x, pos.y, pos.z, volume, pitch, ActionInventoryMod.RANDOM.nextLong()));
	}

	@Override
	public void validate() {
		if (sound==null) sound = Registries.SOUND_EVENT.get(SoundEvents.UI_BUTTON_CLICK.registryKey());
		if (category==null) category = SoundCategory.MASTER;
		if (volume==null) volume = 1f;
		if (pitch==null) pitch = 1f;
		Validated.validate(volume>=0, "Sound action requires volume to be 0 or above, but it is: "+volume);
		Validated.validate(pitch>=0, "Sound action requires pitch to be 0 or above, but it is: "+pitch);
	}

	@Override
	public BasicAction copy() {
		return new SoundAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), sound, category, position, volume, pitch);
	}

	public SoundEvent getSound() {
		return sound;
	}

	public void setSound(SoundEvent sound) {
		this.sound = sound;
	}

	public SoundCategory getCategory() {
		return category;
	}

	public void setCategory(SoundCategory category) {
		this.category = category;
	}

	public Vec3d getPosition() {
		return position;
	}

	public void setPosition(Vec3d position) {
		this.position = position;
	}

	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}

	public Float getPitch() {
		return pitch;
	}

	public void setPitch(Float pitch) {
		this.pitch = pitch;
	}
}