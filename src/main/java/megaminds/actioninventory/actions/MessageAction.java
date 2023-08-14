package megaminds.actioninventory.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.MessageHelper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.network.message.MessageType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

/**
 * Sends a message to specified players
 */
@PolyName("Message")
public final class MessageAction extends BasicAction {	
	private Text message;
	/**
	 * null -> player.uuid
	 */
	private UUID sender;
	/**
	 * Receivers of the message
	 * If null or empty -> broadcast
	 * Contains: nil uuid -> server, null -> player
	 */
	private List<UUID> receivers;
	private Identifier messageType;

	public MessageAction() {}
	
	public MessageAction(Text message, UUID sender, List<UUID> receivers, Identifier messageType) {
		this.message = message;
		this.sender = sender;
		this.receivers = receivers;
		this.messageType = messageType;
	}

	public MessageAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, Text message, UUID sender, List<UUID> receivers, Identifier messageType) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
		this.message = message;
		this.sender = sender;
		this.receivers = receivers;
		this.messageType = messageType;
	}

	@Override
	public void accept(@NotNull ActionInventoryGui gui) {
		var player = gui.getPlayer();
		var server = player.getServer();
		if (sender==null) sender = player.getUuid();

		if (receivers==null || receivers.isEmpty()) {
			MessageHelper.broadcast(sender, message, messageType, server);
			return;
		}

		var temp = new ArrayList<>(receivers);
		if (temp.removeIf(Util.NIL_UUID::equals)) {
			MessageHelper.log(message, server);
		}
		if (temp.removeIf(Objects::isNull)) {
			temp.add(player.getUuid());
		}
		MessageHelper.message(sender, temp, message, messageType, server);
	}

	@Override
	public void validate() {
		if (messageType==null) messageType = MessageType.CHAT.getValue();
		if (message==null) message = Text.empty();		
	}

	@Override
	public BasicAction copy() {
		return new MessageAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), message.copy(), sender, receivers == null ? null : new ArrayList<>(receivers), messageType);
	}

	public Text getMessage() {
		return message;
	}

	public void setMessage(Text message) {
		this.message = message;
	}

	public UUID getSender() {
		return sender;
	}

	public void setSender(UUID sender) {
		this.sender = sender;
	}

	public List<UUID> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<UUID> receivers) {
		this.receivers = receivers;
	}

	public Identifier getMessageType() {
		return messageType;
	}

	public void setMessageType(Identifier messageType) {
		this.messageType = messageType;
	}
}