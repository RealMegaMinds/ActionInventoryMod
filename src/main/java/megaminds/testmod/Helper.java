package megaminds.testmod;

import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class Helper {
	public static void nullToPlayerMessage(Text message, ServerPlayerEntity to) {
		to.sendMessage(message, MessageType.CHAT, Util.NIL_UUID);
	}
	
	public static void serverToPlayerMessage(Text message, ServerPlayerEntity to) {
		to.sendMessage(message, MessageType.SYSTEM, Util.NIL_UUID);
	}
	
	public static void playerToPlayerMessage(ServerPlayerEntity from, Text message, ServerPlayerEntity to) {
		to.sendMessage(message, MessageType.CHAT, from.getUuid());
	}	
	
	public static void playerToServerMessage(ServerPlayerEntity from, Text message) {
		from.getServer().sendSystemMessage(message, from.getUuid());
	}
	
	public static void nullToServerMessage(Text message, MinecraftServer to) {
		to.sendSystemMessage(message, Util.NIL_UUID);
	}
	
	public static void serverToAllMessage(MinecraftServer from, Text message) {
		from.getPlayerManager().broadcastChatMessage(message, MessageType.SYSTEM, Util.NIL_UUID);
	}
	
	public static void nullToAllMessage(MinecraftServer server, Text message) {
		server.getPlayerManager().broadcastChatMessage(message, MessageType.CHAT, Util.NIL_UUID);
	}
	
	public static void playerToAllMessage(ServerPlayerEntity from, Text message) {
		from.getServer().getPlayerManager().broadcastChatMessage(message, MessageType.CHAT, from.getUuid());
	}
	
	public static void nullToPlayerMessage(String message, ServerPlayerEntity to) {
		nullToPlayerMessage(new LiteralText(message), to);
	}
	
	public static void serverToPlayerMessage(String message, ServerPlayerEntity to) {
		serverToPlayerMessage(new LiteralText(message), to);
	}
	
	public static void playerToPlayerMessage(ServerPlayerEntity from, String message, ServerPlayerEntity to) {
		playerToPlayerMessage(from, new LiteralText(message), to);
	}	
	
	public static void playerToServerMessage(ServerPlayerEntity from, String message) {
		playerToServerMessage(from, new LiteralText(message));
	}
	
	public static void nullToServerMessage(String message, MinecraftServer to) {
		nullToServerMessage(new LiteralText(message), to);
	}
	
	public static void serverToAllMessage(MinecraftServer from, String message) {
		serverToAllMessage(from, new LiteralText(message));
	}
	
	public static void nullToAllMessage(MinecraftServer server, String message) {
		nullToAllMessage(server, new LiteralText(message));
	}
	
	public static void playerToAllMessage(ServerPlayerEntity from, String message) {
		playerToAllMessage(from, new LiteralText(message));
	}
	
	public static void playerCommand(ServerPlayerEntity from, String command) {
    	from.getServer().getCommandManager().execute(from.getCommandSource(), command);
	}
	
	public static void serverCommand(MinecraftServer from, String command) {
    	from.getCommandManager().execute(from.getCommandSource(), command);
	}
}