package megaminds.actioninventory.inventory;

import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import megaminds.actioninventory.api.ActionItem;
import megaminds.actioninventory.api.ModifiableActionInventory;
import megaminds.actioninventory.api.gui.ActionInventoryManager;
import megaminds.actioninventory.inventory.openers.Opener;
import megaminds.actioninventory.inventory.openers.Opener.ClickType;
import megaminds.actioninventory.inventory.openers.Opener.What;
import megaminds.actioninventory.util.Helper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * A basic implementation of an ModifiableActionInventory.
 */
public class ActionInventoryImpl implements ModifiableActionInventory {
	private ActionItem[] items;
	private Text displayName;
	private Identifier id;
	
	private int rows;
	private List<Opener> openers;
	private boolean disableCommand;
	private boolean disableAction;
	private boolean disableSign;

	@Override
	public ActionItem getActionItem(int slot) {
		return items[slot];
	}
	
	@Override
	public Text getDisplayName() {
		return displayName;
	}

	@Override
	public int size() {
		return rows*9;
	}

	@Override
	public boolean isEmpty() {
		return items.length!=0 && Helper.getFirst(items, i->!ActionItem.EMPTY.equals(i)) != null;
	}

	@Override
	public @NotNull Identifier getId() {
		return id;
	}
	
	@Override
	public boolean canPlayerUse(ServerPlayerEntity player) {
		//TODO finish
		return false;
	}
	
	@Override
	public void onOpen(ServerPlayerEntity player) {
		//TODO finish
	}

	@Override
	public void onClose(ServerPlayerEntity player) {
		//TODO finish
	}

	@Override
	public boolean containsAny(ActionItem item) {
		return Helper.getFirst(items, i->i.equals(item))!=null;
	}

	@Override
	public JsonElement writeToJson() {
		// TODO finish
		return null;
	}

	@Override
	public void readFromJson(JsonElement obj) {
		// TODO finish
		
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		// TODO finish
		return null;
	}

	@Override
	public void setId(Identifier newId) {
		if (newId==null || ActionInventoryManager.hasInventory(newId)) return;
		this.id = newId;
	}

	@Override
	public void clear() {
		Arrays.parallelSetAll(items, i->ActionItem.EMPTY);
	}

	@Override
	public ActionItem removeActionItem(int slot) {
		return setActionItem(slot, ActionItem.EMPTY);
	}

	@Override
	public ActionItem setActionItem(int slot, ActionItem item) {
		ActionItem old = items[slot];
		items[slot] = item==null ? ActionItem.EMPTY : item;
		return old;
	}

	@Override
	public boolean addActionItem(ActionItem item) {
		return Helper.setFirst(items, i->ActionItem.EMPTY.equals(i), item)!=null;
	}

	@Override
	public boolean isValid(int slot, ActionItem stack) {
		return true;
	}

	@Override
	public void markDirty() {
		//Not sure if I will need this later.
	}

	
	
	
	
	public int getRows() {
		return rows;
	}
	public boolean allowsCommand() {
		return !disableCommand;
	}

	public boolean allowsSign() {
		return !disableSign;
	}

	public boolean allowsAction() {
		return !disableAction;
	}

	public boolean canOpen(ClickType click, What what, Object arg) {
		for (Opener o : openers) {
			if (o.canOpen(arg, click, what)) {
				return true;
			}
		}
		return false;
	}
}