package megaminds.testmod.inventory.actions;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;

/**
 * This plays a sound
 */
public class SoundAction extends Action {
	/** The sound to play*/
	private SoundEvent sound;
	/** The pitch of the sound*/
	private float pitch;
	/** The volume of the sound*/
	private float volume;

	@Override
	public void execute(ServerPlayerEntity player) {
		player.playSound(sound, volume, pitch);
	}
	@Override
	protected Type getTypeInternal() {
		return Type.Sound;
	}
}