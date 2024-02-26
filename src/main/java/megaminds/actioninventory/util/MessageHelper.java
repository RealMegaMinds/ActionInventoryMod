package megaminds.actioninventory.util;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import static megaminds.actioninventory.util.Helper.getPlayer;

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
	public static void message(@Nullable UUID from, List<UUID> to, Text message, @Nullable Identifier type, MinecraftServer server) {
		if (from == null) {
			message = Placeholders.parseText(message, PlaceholderContext.of(server));
			for (var uuid : to) {
				getPlayer(server, uuid).sendMessage(message);
			}
		} else {
			for (var uuid : to) {
				var p = getPlayer(server, from);
				message = Placeholders.parseText(message, PlaceholderContext.of(p));

				var msg = SentMessage.of(SignedMessage.ofUnsigned(from, "").withUnsignedContent(message));
				var source = p.getCommandSource();
				var params = MessageType.params(idToKey(type, server).orElse(MessageType.CHAT), source);
				var reciever = getPlayer(server, uuid);
				reciever.sendChatMessage(msg, source.shouldFilterText(reciever), params);
			}
		}
	}

	private static Optional<RegistryKey<MessageType>> idToKey(Identifier id, MinecraftServer server) {
		var reg = server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE);
		return reg.getKey(reg.get(id));
	}

	/**
	 * Broadcasts a message to all players.
	 */
	public static void broadcast(@Nullable UUID from, Text message, @Nullable Identifier type, MinecraftServer server) {
		if (from == null) {
			message = Placeholders.parseText(message, PlaceholderContext.of(server));
			server.getPlayerManager().broadcast(message, false);
		} else {
			var p = getPlayer(server, from);
			message = Placeholders.parseText(message, PlaceholderContext.of(p));

			var msg = SignedMessage.ofUnsigned(from, "").withUnsignedContent(message);
			var source = p.getCommandSource();
			var params = MessageType.params(idToKey(type, server).orElse(MessageType.CHAT), source);
			server.getPlayerManager().broadcast(msg, source, params);
		}
	}

	/**
	 * Logs a message to the server.
	 */
	public static void log(Text message, MinecraftServer server) {
		server.sendMessage(Placeholders.parseText(message, PlaceholderContext.of(server)));
	}

	/**
	 * Executes the given command as the server.
	 */
	public static void executeCommand(MinecraftServer server, String command) {
		executeCommand(server.getCommandSource(), command);
	}

	/**
	 * Executes the given command as the player.<br>
	 * Command may fail if the player has incorrect permissions.
	 */
	public static void executeCommand(ServerPlayerEntity player, String command) {
		executeCommand(player.getCommandSource(), command);
	}

	public static void executeCommand(ServerCommandSource source, String command) {
		source.getServer().getCommandManager().executeWithPrefix(source, Placeholders.parseText(TextNode.of(command), PlaceholderContext.of(source)).getString());
	}

	/**
	 * Executes the given command as the given player.<br>
	 * If the player was not already an op, they made an op before executing the command and deopped after completing the command.
	 */
	public static void executeOppedCommand(ServerPlayerEntity player, String command) {
		PlayerManager manager = player.getServer().getPlayerManager();
		GameProfile profile = player.getGameProfile();
		boolean wasOp = manager.isOperator(profile);

		if (!wasOp) manager.addToOperators(profile);
		executeCommand(player, command);
		if (!wasOp) manager.removeFromOperators(profile);
	}
}