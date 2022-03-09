package megaminds.actioninventory.util;

import java.util.Optional;
import java.util.UUID;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.ActionInventoryMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

/**
 * This is just a helper class for consumables working with PlayerDataApi.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsumableDataHelper {
	private static final PlayerDataStorage<NbtCompound> CONSUMABLE_DATA = new NbtDataStorage(ActionInventoryMod.MOD_ID);

	static {
		PlayerDataApi.register(CONSUMABLE_DATA);
	}

	public static void setPlayer(MinecraftServer server, UUID player, NbtCompound value) {
		PlayerDataApi.setCustomDataFor(server, player, CONSUMABLE_DATA, value);
	}

	public static void removePlayer(MinecraftServer server, UUID player) {
		PlayerDataApi.setCustomDataFor(server, player, CONSUMABLE_DATA, null);
	}

	public static Optional<NbtCompound> getPlayer(MinecraftServer server, UUID player) {
		return Optional.ofNullable(PlayerDataApi.getCustomDataFor(server, player, CONSUMABLE_DATA));
	}

	public static NbtCompound getOrCreatePlayer(MinecraftServer server, UUID player) {
		var store = getPlayer(server, player).orElse(null);
		if (store==null) {
			store = new NbtCompound();
			PlayerDataApi.setCustomDataFor(server, player, CONSUMABLE_DATA, store);
		}
		return store;
	}

	public static void setGui(MinecraftServer server, UUID player, String guiName, NbtCompound value) {
		getOrCreatePlayer(server, player).put(guiName, value);
	}

	public static void removeGui(MinecraftServer server, UUID player, String guiName) {
		getPlayer(server, player).ifPresent(n->n.remove(guiName));
	}

	public static Optional<NbtCompound> getGui(MinecraftServer server, UUID player, String guiName) {
		return getPlayer(server, player).filter(n->n.contains(guiName)).map(n->n.getCompound(guiName));
	}

	public static NbtCompound getOrCreateGui(MinecraftServer server, UUID player, String guiName) {
		return Helper.computeIfAbsent(getOrCreatePlayer(server, player), s->new NbtCompound(), guiName);
	}

	public static void removeAction(MinecraftServer server, UUID player, String guiName, String lastAction, NbtCompound data) {
		getOrCreateGui(server, player, guiName).put(lastAction, data);
	}

	public static void setAction(MinecraftServer server, UUID player, String guiName, String lastAction) {
		getGui(server, player, guiName).ifPresent(n->n.remove(lastAction));
	}

	public static Optional<NbtCompound> getAction(MinecraftServer server, UUID player, String guiName, String lastAction) {
		return getGui(server, player, guiName).filter(n->n.contains(lastAction)).map(n->n.getCompound(lastAction));
	}

	public static NbtCompound getOrCreateAction(MinecraftServer server, UUID player, String guiName, String lastAction) {
		return Helper.computeIfAbsent(getOrCreateGui(server, player, guiName), s->new NbtCompound(), lastAction);
	}

	public static void setConsumable(MinecraftServer server, UUID player, String guiName, String lastAction, String consumable, NbtCompound data) {
		getOrCreateAction(server, player, guiName, lastAction).put(consumable, data);
	}

	public static void removeConsumable(MinecraftServer server, UUID player, String guiName, String lastAction, String consumable) {
		getAction(server, player, guiName, lastAction).ifPresent(n->n.remove(consumable));
	}

	public static Optional<NbtCompound> getConsumable(MinecraftServer server, UUID player, String guiName, String lastAction, String consumable) {
		return getAction(server, player, guiName, lastAction).filter(n->n.contains(consumable)).map(n->n.getCompound(consumable));
	}

	public static NbtCompound getOrCreateConsumable(MinecraftServer server, UUID player, String guiName, String lastAction, String consumable) {
		return Helper.computeIfAbsent(getOrCreateAction(server, player, guiName, lastAction), s->new NbtCompound(), consumable);
	}
}