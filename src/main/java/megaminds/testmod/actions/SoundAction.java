package megaminds.testmod.actions;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;

/**
 * This plays a sound
 */
public class SoundAction implements Action {
    private final SoundEvent sound;
    private final float pitch;
    private final float volume;
    
    /**
     * @param sound
     * The sound to play
     * @param pitch
     * The pitch of the sound
     * @param volume
     * The volume of the sound
     */
	public SoundAction(SoundEvent sound, float pitch, float volume) {
		this.sound = sound;
		this.pitch = pitch;
		this.volume = volume;
	}

	@Override
	public void execute(ServerPlayerEntity player) {
        player.playSound(sound, volume, pitch);
	}
}