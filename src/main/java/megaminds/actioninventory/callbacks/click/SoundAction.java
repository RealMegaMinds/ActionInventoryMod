package megaminds.actioninventory.callbacks.click;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * This plays a sound
 */
public class SoundAction extends BasicAction {
	private static final String SOUND = "sound", CATEGORY = "category", POS = "pos", VOLUME = "volume", PITCH = "pitch";
	
	private Identifier sound;
	private SoundCategory category;
	private Vec3d pos;
	private float volume;
	private float pitch;

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(sound, category, pos!=null ? pos : player.getPos(), volume, pitch));
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		sound = obj.has(SOUND) ? new Identifier(obj.get(SOUND).getAsString()) : SoundEvents.UI_BUTTON_CLICK.getId();
		category = obj.has(CATEGORY) ? context.deserialize(obj.get(CATEGORY), SoundCategory.class) : SoundCategory.MASTER;
		pos = obj.has(POS) ? getPosFromJson(obj.get(POS).getAsJsonObject()) : null;
		volume = obj.has(VOLUME) ? obj.get(VOLUME).getAsFloat() : 1;
		pitch = obj.has(PITCH) ? obj.get(PITCH).getAsFloat() : 1;
		return this;
	}
	
	private static Vec3d getPosFromJson(JsonObject obj) {
		int x = obj.has("x") ? obj.get("x").getAsInt() : 0;
		int y = obj.has("y") ? obj.get("y").getAsInt() : 0;
		int z = obj.has("z") ? obj.get("z").getAsInt() : 0;
		return new Vec3d(x, y, z);
	}
}