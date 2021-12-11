package megaminds.actioninventory.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * An object that has a ItemStack representation and does something when clicked.
 */
public interface ActionItem {
	/**
	 * An ActionItem whose representation is ItemStack.EMPTY and does nothing when clicked.
	 */
	public static final ActionItem EMPTY = new ActionItem() {
		@Override public JsonElement writeToJson() {return JsonNull.INSTANCE;}
		@Override public void readFromJson(JsonElement obj) {}
		@Override public ItemStack getRepresentation() {return ItemStack.EMPTY;}
		@Override public boolean canView(ServerPlayerEntity player) {return true;}
		@Override public void onClick(ServerPlayerEntity player) {}
	};
	
	/**
	 * Writes this inventory to a JsonElement.
	 */
	JsonElement writeToJson();
	
	/**
	 * Reads this inventory from a given JsonElement.
	 */
	void readFromJson(JsonElement obj);
	
	/**
	 * Returns the ItemStack representation of this ActionItem.
	 */
	ItemStack getRepresentation();
	
	/**
	 * Returns true if the given player can see this ActionItem in the inventory.
	 */
	boolean canView(ServerPlayerEntity player);
	
	/**
	 * Called when this ActionItem is clicked on.
	 */
	void onClick(ServerPlayerEntity player);
}