package megaminds.actioninventory.inventory.actions;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * This plays a sound
 */
public class SoundAction extends Action {
	/** The sound to play*/
	private Identifier sound;
	/** The pitch of the sound*/
	private float pitch;
	/** The volume of the sound*/
	private float volume;

	@Override
	public void execute(ServerPlayerEntity player) {
		player.playSound(Registry.SOUND_EVENT.get(sound), volume, pitch);
	}
	@Override
	protected Type getTypeInternal() {
		return Type.Sound;
	}
}