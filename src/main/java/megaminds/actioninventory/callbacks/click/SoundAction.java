package megaminds.actioninventory.callbacks.click;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

/**
 * This plays a sound
 */
public class SoundAction implements ClickCallback {
	/** The sound to play*/
	private Identifier sound;
	/** The pitch of the sound*/
	private float pitch;
	/** The volume of the sound*/
	private float volume;

	@Override
	public void click(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(sound, SoundCategory.PLAYERS, player.getPos(), volume, pitch));
	}
}