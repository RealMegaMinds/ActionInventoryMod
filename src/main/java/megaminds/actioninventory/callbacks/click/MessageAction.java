package megaminds.actioninventory.callbacks.click;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.util.MessageHelper;
import net.minecraft.network.MessageType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/**
 * Sends a message to specified players
 */
public class MessageAction extends BasicAction {
	private static final String MESSAGE = "message", SENDER = "sender", RECIEVERS = "recievers", MESSAGE_TYPE = "messageType";
	private static final String SERVER = "server", PLAYER = "player", BROADCAST = "broadcast";
	
	private Text msg;
	private UUID from;
	private List<UUID> to;
	private MessageType type;
			
	@Override
	public void internalClick(int index, ClickType cType, SlotActionType action, SlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		from = from!=null ? from : player.getUuid();
		if (to==null) {
			MessageHelper.broadcast(from, msg, type, player.getServer());
			return;
		}
		if (to.contains(Util.NIL_UUID)) {
			MessageHelper.log(from, msg, player.getServer());
			to.remove(Util.NIL_UUID);
		}
		if (to.contains(null)) {
			to.remove(null);
			to.add(player.getUuid());
		}
		MessageHelper.message(from, to, msg, type, player.getServer());
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		msg = obj.has(MESSAGE) ? Text.Serializer.fromJson(obj.get(MESSAGE)) : new LiteralText("");
		from = obj.has(SENDER) ? uuidFromStr(obj.get(SENDER).toString()) : Util.NIL_UUID;
		to = obj.has(RECIEVERS) ? getTo(obj.get(RECIEVERS)) : List.of((UUID)null);
		type = obj.has(MESSAGE_TYPE) ? context.deserialize(obj.get(MESSAGE_TYPE), MessageType.class) : null;
		return this;
	}
	
	private List<UUID> getTo(JsonElement el) {
		if (el instanceof JsonArray arr) {
			List<UUID> list = new ArrayList<>();
			for (JsonElement uuid : arr) {
				list.add(uuidFromStr(uuid.getAsString()));
			}
			return list;
		} else {
			String s = el.getAsString();
			if (s.equals(BROADCAST)) {
				return null;
			} else {
				return List.of(uuidFromStr(s));
			}
		}
	}
	
	private static UUID uuidFromStr(String fromStr) {
		if (fromStr.equals(PLAYER)) {
			return null;
		} else if (fromStr.equals(SERVER)) {
			return Util.NIL_UUID;
		} else {
			return UUID.fromString(fromStr);
		}
	}
}