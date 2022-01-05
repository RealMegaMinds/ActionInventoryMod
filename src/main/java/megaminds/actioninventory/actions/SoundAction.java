package megaminds.actioninventory.actions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.JsonHelper;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
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
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		obj.addProperty(SOUND, sound.toString());
		obj.add(CATEGORY, context.serialize(category));
		obj.add(POS, context.serialize(pos));
		obj.addProperty(VOLUME, volume);
		obj.addProperty(PITCH, pitch);
		return obj;
	}


	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(sound, category, pos!=null ? pos : player.getPos(), volume, pitch));
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		sound = new Identifier(JsonHelper.getOrDefault(obj.get(SOUND), JsonElement::getAsString, "ui.button.click"));
		category = JsonHelper.getOrDefault(obj.get(CATEGORY), SoundCategory.class, context::deserialize, SoundCategory.MASTER);
		pos = JsonHelper.getOrDefault(obj.get(POS), Vec3d.class, context::deserialize, null);
		volume = JsonHelper.getOrDefault(obj.get(VOLUME), JsonElement::getAsFloat, 1f);
		pitch = JsonHelper.getOrDefault(obj.get(PITCH), JsonElement::getAsFloat, 1f);
		return this;
	}
	
	@Override
	public Action getType() {
		return Action.SOUND;
	}
}