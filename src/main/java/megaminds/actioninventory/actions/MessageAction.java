package megaminds.actioninventory.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import eu.pb4.sgui.api.ClickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.MessageHelper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.network.MessageType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

/**
 * Sends a message to specified players
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
	private MessageType messageType;

	public MessageAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, Text message, UUID sender, List<UUID> receivers, MessageType messageType) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
		this.message = message;
		this.sender = sender;
		this.receivers = receivers;
		this.messageType = messageType;
	}

	@Override
	public void accept(ActionInventoryGui gui) {
		var player = gui.getPlayer();
		var server = player.getServer();
		if (sender==null) sender = player.getUuid();

		if (receivers==null || receivers.isEmpty()) {
			MessageHelper.broadcast(sender, message, messageType, server);
			return;
		}

		var temp = new ArrayList<>(receivers);
		if (temp.removeIf(Util.NIL_UUID::equals)) {
			MessageHelper.log(sender, message, server);
		}
		if (temp.removeIf(Objects::isNull)) {
			temp.add(player.getUuid());
		}
		MessageHelper.message(sender, temp, message, messageType, server);
	}

	@Override
	public void validate() {
		if (messageType==null) messageType = MessageType.CHAT;
		if (message==null) message = LiteralText.EMPTY;		
	}

	@Override
	public BasicAction copy() {
		return new MessageAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), message.shallowCopy(), sender, new ArrayList<>(receivers), messageType);
	}
}