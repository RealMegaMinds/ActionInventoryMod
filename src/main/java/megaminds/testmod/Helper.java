package megaminds.testmod;

import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

public class Helper {
	public static void nullToPlayerMessage(String message, ServerPlayerEntity to) {
		to.sendMessage(new LiteralText(message), MessageType.CHAT, Util.NIL_UUID);
	}
	
	public static void serverToPlayerMessage(String message, ServerPlayerEntity to) {
		to.sendMessage(new LiteralText(message), MessageType.SYSTEM, Util.NIL_UUID);
	}
	
	public static void playerToPlayerMessage(ServerPlayerEntity from, String message, ServerPlayerEntity to) {
		to.sendMessage(new LiteralText(message), MessageType.CHAT, from.getUuid());
	}	
	
	public static void playerToServerMessage(ServerPlayerEntity from, String message) {
		from.getServer().sendSystemMessage(new LiteralText(message), from.getUuid());
	}
	
	public static void nullToServerMessage(String message, MinecraftServer to) {
		to.sendSystemMessage(new LiteralText(message), Util.NIL_UUID);
	}
	
	public static void serverToAllMessage(MinecraftServer from, String message) {
		from.getPlayerManager().broadcastChatMessage(new LiteralText(message), MessageType.SYSTEM, Util.NIL_UUID);
	}
	
	public static void nullToAllMessage(MinecraftServer server, String message) {
		server.getPlayerManager().broadcastChatMessage(new LiteralText(message), MessageType.CHAT, Util.NIL_UUID);
	}
	
	public static void playerToAllMessage(ServerPlayerEntity from, String message) {
		from.getServer().getPlayerManager().broadcastChatMessage(new LiteralText(message), MessageType.CHAT, from.getUuid());
	}
	
	public static void playerCommand(ServerPlayerEntity from, String command) {
    	from.getServer().getCommandManager().execute(from.getCommandSource(), command);
	}
	
	public static void serverCommand(MinecraftServer from, String command) {
    	from.getCommandManager().execute(from.getCommandSource(), command);
	}
}