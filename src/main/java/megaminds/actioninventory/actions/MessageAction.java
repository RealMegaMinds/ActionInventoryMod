package megaminds.actioninventory.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.MessageHelper;
import megaminds.actioninventory.util.annotations.TypeName;
import net.minecraft.network.MessageType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/**
 * Sends a message to specified players
 */
@TypeName("Message")
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
	
	private MessageAction() {}
			
	@Override
	public void internalClick(int index, ClickType cType, SlotActionType action, NamedSlotGuiInterface gui) {
		ServerPlayerEntity player = gui.getPlayer();
		MinecraftServer server = player.getServer();
		if (sender==null) sender = player.getUuid();
		
		if (messageType==null) messageType = MessageType.CHAT;
		if (message==null) message = LiteralText.EMPTY;
		
		if (receivers==null || receivers.isEmpty()) {
			MessageHelper.broadcast(sender, message, messageType, server);
			return;
		}
		
		List<UUID> temp = new ArrayList<>(receivers);
		if (temp.removeIf(Util.NIL_UUID::equals)) {
			MessageHelper.log(sender, message, server);
		}
		if (temp.removeIf(Objects::isNull)) {
			temp.add(player.getUuid());
		}
		MessageHelper.message(sender, temp, message, messageType, server);
	}
}