package megaminds.actioninventory.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import megaminds.actioninventory.api.gui.ActionInventory;
import megaminds.actioninventory.api.gui.ActionInventoryManager;
import net.minecraft.util.Identifier;

/**
 * Represents a modifiable ActionInventory.
 */
public interface ModifiableActionInventory extends ActionInventory {
	/**
	 * Sets this inventory's id to the given one.<br>
	 * ActionInventoryManager should not contain any other inventories with this id.
	 * @see ActionInventoryManager#hasInventory(Identifier)
	 */
	void setId(Identifier newId);
	
	/**
	 * Clears all ActionItems in this inventory.
	 */
	void clear();

	/**
	 * Sets the given slot to {@link ActionItem#EMPTY} and returns the ActionItem that was there.
	 */
	ActionItem removeActionItem(int slot);

	/**
	 * Sets the given slot to the given ActionItem and returns the ActionItem that was there.
	 */
	ActionItem setActionItem(int slot, ActionItem item);
	
	/**
	 * Sets the first empty slot to the given ActionItem and returns true. <br>
	 * If there are no empty slots, returns false;
	 */
	boolean addActionItem(ActionItem item);

	/**
	 * Returns true if the given ActionItem can be placed in the given slot.<br>
	 * Returns false if the ActionItem cannot be placed in the slot.
	 */
	boolean isValid(int slot, ActionItem stack);
	
	/**
	 * Sets the size of this inventory. 
	 */
	void setSize(int size);

	/**
	 * Marks this inventory dirty.
	 */
	@Override
	void markDirty();
		
	@Override
	default void readFromJson(JsonElement e) {
		JsonObject obj = e.getAsJsonObject();
		setId(new Identifier(obj.get(ID_KEY).getAsString()));
	}
}