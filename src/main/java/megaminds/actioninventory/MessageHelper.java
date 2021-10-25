package megaminds.actioninventory;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class MessageHelper {
	private static final Formatting ERROR = Formatting.RED, SUCCESS = Formatting.GREEN;
	
	public static Text toSuccess(String msg) {
		return new LiteralText(msg).setStyle(Style.EMPTY.withColor(SUCCESS));
	}
	
	public static Text toError(String error) {
		return new LiteralText(error).setStyle(Style.EMPTY.withColor(ERROR));
	}
	
	public static void multiMessage(ServerPlayerEntity player, Text message, List<UUID> to, boolean fromServer) {
		MessageType type = getType(fromServer);
		UUID from = getOrNil(player, fromServer);
		to.forEach(uuid -> {
			player.getServer().getPlayerManager().getPlayer(uuid).sendMessage(message, type, from);
		});
	}
	
	public static void multiMessage(ServerPlayerEntity player, String message, List<UUID> to, boolean fromServer) {
		multiMessage(player, new LiteralText(message), to, fromServer);
	}
	
	public static void singleMessage(ServerPlayerEntity player, Text message, UUID to, boolean fromServer) {
		player.getServer().getPlayerManager().getPlayer(to).sendMessage(message, getType(fromServer), getOrNil(player, fromServer));
	}
	
	public static void singleMessage(ServerPlayerEntity player, String message, UUID to, boolean fromServer) {
		singleMessage(player, new LiteralText(message), to, fromServer);
	}
	
	public static void toPlayerMessage(ServerPlayerEntity player, Text message, boolean fromServer) {
		player.sendMessage(message, getType(fromServer), Util.NIL_UUID);
	}
	
	public static void toPlayerMessage(ServerPlayerEntity player, String message, boolean fromServer) {
		toPlayerMessage(player, new LiteralText(message), fromServer);
	}
	
	public static void broadcastMessage(ServerPlayerEntity player, Text message, boolean fromServer) {
		player.getServer().getPlayerManager().broadcastChatMessage(message, getType(fromServer), getOrNil(player, fromServer));
	}
	
	public static void broadcastMessage(ServerPlayerEntity player, String message, boolean fromServer) {
		broadcastMessage(player, new LiteralText(message), fromServer);
	}
	
	public static void logMessage(ServerPlayerEntity player, Text message, boolean fromPlayer) {
		player.getServer().sendSystemMessage(message, getOrNil(player, !fromPlayer));
	}
	
	public static void logMessage(ServerPlayerEntity player, String message, boolean fromPlayer) {
		logMessage(player, new LiteralText(message), fromPlayer);
	}

	private static MessageType getType(boolean fromServer) {
		return fromServer ? MessageType.SYSTEM : MessageType.CHAT;
	}
	
	private static UUID getOrNil(ServerPlayerEntity player, boolean fromServer) {
		return fromServer ? Util.NIL_UUID : player.getUuid();
	}
	
	public static int executeCommand(ServerPlayerEntity player, String command, boolean fromServer) {
		MinecraftServer server = player.getServer();
    	return server.getCommandManager().execute(fromServer ? server.getCommandSource() : player.getCommandSource(), command);
	}
}