package megaminds.testmod;

import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class Helper {
	/**
	 * if toAll:
	 * True - if fromServer
	 * 			True - server to all
	 * 			False - if from==null
	 * 					True - nil to all
	 * 					False - player to all
	 * 
	 * False - if fromServer
	 * 			True - server to player
	 * 			False - if from==null
	 * 					True - nil to player
	 * 					False - player to player
	 */
	public static void sendMessage(@Nullable ServerPlayerEntity from, Text message, @NotNull ServerPlayerEntity to, boolean fromServer, boolean toAll) {
		Objects.requireNonNull(to);
		
		MessageType type 	= 	fromServer ? MessageType.SYSTEM : 	MessageType.CHAT;
		UUID fromUuid 		= 	fromServer ? Util.NIL_UUID		:	getOrNil(from);
		if (toAll) {
			to.getServer().getPlayerManager().broadcastChatMessage(message, type, fromUuid);
		} else {
			to.sendMessage(message, type, fromUuid);
		}
	}
	
	public static void sendMessage(@NotNull ServerPlayerEntity from, Text message, @NotNull UUID to, boolean fromServer) {
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		
		MessageType type 	= 	fromServer ? MessageType.SYSTEM : 	MessageType.CHAT;
		UUID fromUuid 		= 	fromServer ? Util.NIL_UUID		:	getOrNil(from);
		from.getServer().getPlayerManager().broadcastChatMessage(message, type, fromUuid);
	}
	
	public static void sendMessage(@Nullable ServerPlayerEntity from, String message, @NotNull ServerPlayerEntity to, boolean fromServer, boolean toAll) {
		sendMessage(from, new LiteralText(message), to, fromServer, toAll);
	}
	
	public static void logMessage(@NotNull ServerPlayerEntity from, Text message, boolean useNil) {
		Objects.requireNonNull(from);
		from.getServer().sendSystemMessage(message, useNil ? Util.NIL_UUID : from.getUuid());
	}
	
	public static void logMessage(@NotNull ServerPlayerEntity from, String message, boolean useNil) {
		logMessage(from, new LiteralText(message), useNil);
	}
	
	private static UUID getOrNil(UUID uuid) {
		return uuid==null?Util.NIL_UUID:uuid;
	}
	
	private static UUID getOrNil(ServerPlayerEntity player) {
		return getOrNil(player.getUuid());
	}
	
	public static int playerCommand(ServerPlayerEntity from, String command) {
    	return from.getServer().getCommandManager().execute(from.getCommandSource(), command);
	}
	
	public static int serverCommand(MinecraftServer from, String command) {
    	return from.getCommandManager().execute(from.getCommandSource(), command);
	}
}