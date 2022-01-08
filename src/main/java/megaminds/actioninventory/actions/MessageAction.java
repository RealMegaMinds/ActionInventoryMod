package megaminds.actioninventory.actions;

import static megaminds.actioninventory.util.JsonHelper.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.MessageHelper;
import net.minecraft.network.MessageType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
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
	public void internalClick(int index, ClickType cType, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		MinecraftServer server = player.getServer();
		from = Objects.requireNonNullElse(from, player.getUuid());
		if (to.isEmpty()) {
			MessageHelper.broadcast(from, msg, type, server);
			return;
		}
		
		List<UUID> temp = new ArrayList<>(to);
		if (temp.removeIf(Util.NIL_UUID::equals)) {
			MessageHelper.log(from, msg, server);
		}
		if (temp.removeIf(Objects::isNull)) {
			temp.add(player.getUuid());
		}
		MessageHelper.message(from, temp, msg, type, server);
	}

	@Override
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		msg = text(obj.get(MESSAGE));
		from = custom(obj.get(SENDER), MessageAction::uuid, Util.NIL_UUID);
		to = custom(obj.get(RECIEVERS), MessageAction::getTo, List.of((UUID)null));
		type = clazz(obj.get(MESSAGE_TYPE), MessageType.class, context, MessageType.CHAT);
		return this;
	}
	
	private static List<UUID> getTo(JsonElement el) {
		JsonArray arr = array(el);
		if (arr.size()==1&&arr.getAsString().equals(BROADCAST)) {
			return Collections.emptyList();
		} else {
			return customList(arr, MessageAction::uuid, true);
		}
	}
	
	private static UUID uuid(JsonElement e) {
		String fromStr = string(e);
		if (fromStr.equals(PLAYER)) {
			return null;
		} else if (fromStr.equals(SERVER)) {
			return Util.NIL_UUID;
		} else {
			return UUID.fromString(fromStr);
		}
	}

	@Override
	public JsonObject toJson(JsonObject obj, JsonSerializationContext context) {
		obj.add(MESSAGE, Text.Serializer.toJsonTree(msg));
		obj.addProperty(SENDER, Helper.apply(from, UUID::toString));
		obj.add(RECIEVERS, context.serialize(Helper.mapEach(to, UUID::toString, true)));
		obj.add(MESSAGE_TYPE, context.serialize(type));
		return obj;
	}

	@Override
	public Action getType() {
		return Action.MESSAGE;
	}
}