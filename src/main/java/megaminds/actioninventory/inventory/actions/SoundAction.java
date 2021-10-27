package megaminds.actioninventory.inventory.actions;

import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

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
		player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(sound, SoundCategory.PLAYERS, player.getPos(), volume, pitch));
	}
	@Override
	protected Type getTypeInternal() {
		return Type.Sound;
	}
}