package megaminds.actioninventory.util;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryKey;

public class MessageHelper {
	private static final Formatting ERROR = Formatting.RED;
	private static final Formatting SUCCESS = Formatting.GREEN;
	
	private MessageHelper() {}
	
	public static MutableText toSuccess(String msg) {
		return Text.literal(msg).formatted(SUCCESS);
	}
	
	public static MutableText toError(String error) {
		return Text.literal(error).formatted(ERROR);
	}
	
	/**
	 * Sends a message to the given players.
	 */
	public static void message(@Nullable UUID from, List<UUID> to, Text message, @Nullable MessageType type, MinecraftServer server) {
		from = from!=null ? from : Util.NIL_UUID;
		type = type!=null ? type : getType(from);
		
		for (UUID uuid : to) {
			server.getPlayerManager().getPlayer(uuid).message;
			server.getPlayerManager().getPlayer(uuid).sendMessage(message, type, from);
		}
	}
	
	/**
	 * Broadcasts a message to all players.
	 */
	public static void broadcast(@Nullable UUID from, Text message, @Nullable RegistryKey<MessageType> type, MinecraftServer server) {
		if (from==null) {
			server.getPlayerManager().broadcast(message, type!=null ? type : MessageType.SYSTEM);
		} else {
			//TODO change so player is sending it
			server.getPlayerManager().broadcast(message, type!=null ? type : MessageType.CHAT);
		}
		SayCommand
	}
	
	/**
	 * Logs a message to the server.
	 */
	public static void log(Text message, MinecraftServer server) {
		server.sendMessage(message);
	}
	
	/**
	 * Executes the given command as the server.
	 */
	public static int executeCommand(MinecraftServer server, String command) {
		return server.getCommandManager().execute(server.getCommandSource(), command);
	}
	
	/**
	 * Executes the given command as the player.<br>
	 * Command may fail if the player has incorrect permissions.
	 */
	public static int executeCommand(ServerPlayerEntity player, String command) {
    	return player.getServer().getCommandManager().execute(player.getCommandSource(), command);
	}
	
	public static int executeCommand(ServerCommandSource source, String command) {
		return source.getServer().getCommandManager().execute(source, command);
	}
	
	/**
	 * Executes the given command as the given player.<br>
	 * If the player was not already an op, they made an op before executing the command and deopped after completing the command.
	 */
	public static int executeOppedCommand(ServerPlayerEntity player, String command) {
		PlayerManager manager = player.getServer().getPlayerManager();
		GameProfile profile = player.getGameProfile();
		boolean wasOp = manager.isOperator(profile);
		
		if (!wasOp) manager.addToOperators(profile);
		int result = executeCommand(player, command);
		if (!wasOp) manager.removeFromOperators(profile);
		
		return result;
	}
}