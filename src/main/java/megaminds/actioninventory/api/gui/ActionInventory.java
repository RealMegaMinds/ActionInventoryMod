package megaminds.actioninventory.api.gui;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import megaminds.actioninventory.api.ActionItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * An inventory that contains ActionItems.<br>
 * Needs to be added to ActionInventoryManager to be used.<br>
 * If adding more extra fields, consider overriding {@link #writeToJson()} and {@link #readFromJson(JsonObject)}
 * 
 * Extends Inventory for Slot constructor. Most methods from Inventory have been defaulted to do nothing.
 * 
 * @see ActionInventoryManager#addInventory(ActionInventory)
 */
public interface ActionInventory extends Inventory, NamedScreenHandlerFactory {
	public static final String ID_KEY = "id", ITEM_KEY = "items";
	
	/**
	 * Returns the Id for this inventory. Ids should be unique to reduce conflicts.
	 */
	@NotNull
	Identifier getId();

	/**
	 * Returns the size of this inventory. Includes empty slots.
	 */
	int size();

	/**
	 * Returns true if there are any items in this inventory.<br>
	 * Returns false if size==0 or all slots are empty.
	 */
	boolean isEmpty();

	/**
	 * Returns the ActionItem at the requested slot.
	 */
	ActionItem getActionItem(int slot);

	/**
	 * Returns true if the given player can open/use this inventory.<br>
	 * Returns false if the player cannot open/use this inventory.
	 */
	boolean canPlayerUse(ServerPlayerEntity player);

	/**
	 * Called when an inventory is opened by the specified player.
	 */
	void onOpen(ServerPlayerEntity player);

	/**
	 * Called when an inventory is closed by the specified player.
	 */
	void onClose(ServerPlayerEntity player);

	/**
	 * Returns true if this inventory contains the given ActionItem.<br>
	 * Returns false if this inventory does not contain the ActionItem.
	 */
	boolean containsAny(ActionItem items);

	/**
	 * Writes this inventory to a JsonElement.<br>
	 * Default implementation adds the id and action items.
	 */
	default JsonElement writeToJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty(ID_KEY, getId().toString());
		JsonArray arr = new JsonArray();
		for (int i = 0; i < size(); i++) {
			arr.add(getActionItem(i).writeToJson());
		}
		obj.add(ITEM_KEY, arr);
		return obj;
	}

	/**
	 * Reads this inventory from a given JsonElement
	 */
	default void readFromJson(JsonElement obj) {
	}
	
	//******Overrided From NamedScreenHandlerFactory***************************************
	
	/**
	 * Returns a ScreenHandler for this inventory.
	 */
	@Override
	ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity);

	/**
	 * Returns the display name of this Inventory.
	 */
	@Override
	Text getDisplayName();
	
	//******Overrided From Inventory*****************

	/**
	 * Does nothing. Returns {@link ItemStack#EMPTY}.
	 */
	@Override
	default ItemStack getStack(int var1) {
		return ItemStack.EMPTY;
	}

	/**
	 * Does nothing. Returns {@link ItemStack#EMPTY}.
	 */
	@Override
	default ItemStack removeStack(int slot, int amount) {
		return ItemStack.EMPTY;
	}

	/**
	 * Does nothing. Returns {@link ItemStack#EMPTY}.
	 */
	@Override
	default ItemStack removeStack(int slot) {
		return ItemStack.EMPTY;
	}

	/**
	 * Does nothing.
	 */
	@Override
	default void setStack(int slot, ItemStack stack) {
	}
	
	/**
	 * Does nothing.
	 */
	@Override
	default void markDirty() {
	}

	/**
	 * Delegates to {@link #canPlayerUse(ServerPlayerEntity)}.
	 */
	@Override
	default boolean canPlayerUse(PlayerEntity player) {
		return canPlayerUse((ServerPlayerEntity)player);
	}

	/**
	 * Does nothing.
	 */
	@Override
	default void clear() {
	}

	/**
	 * Does nothing. Returns {@link Integer#MAX_VALUE}
	 */
	@Override
	default int getMaxCountPerStack() {
		return Integer.MAX_VALUE;
	}
	
	/**
	 * Delegates to {@link #onOpen(ServerPlayerEntity)}.
	 */
	@Override
	default void onOpen(PlayerEntity player) {
		onOpen((ServerPlayerEntity)player);
	}

	/**
	 * Delegates to {@link #onClose(ServerPlayerEntity)}.
	 */
	@Override
	default void onClose(PlayerEntity player) {
		onClose((ServerPlayerEntity)player);
	}

	/**
	 * Does nothing. Returns false.
	 */
	@Override
	default boolean isValid(int slot, ItemStack stack) {
		return false;
	}

	/**
	 * Does nothing. Returns 0.
	 */
	@Override
	default int count(Item item) {
		return 0;
	}

	/**
	 * Does nothing. Returns false.
	 */
	@Override
	default boolean containsAny(Set<Item> items) {
		return false;
	}
}