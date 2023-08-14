package megaminds.actioninventory.actions;

import org.jetbrains.annotations.NotNull;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.MessageHelper;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType.NbtPath;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**@since 3.6*/
@PolyName("AnvilStore")
public final class AnvilStoreAction extends BasicAction {
	private String storageId;
	private String path;
	
	public AnvilStoreAction() {
	}
	
	public AnvilStoreAction(String storageId, String path) {
		this.storageId = storageId;
		this.path = path;
	}
	
	public AnvilStoreAction(Integer requiredIndex, ClickType requiredClickType, SlotActionType requiredSlotActionType, TriState requireShift, Identifier requiredRecipe, Identifier requiredGuiName, String storageId, String path) {
		super(requiredIndex, requiredClickType, requiredSlotActionType, requireShift, requiredRecipe, requiredGuiName);
		this.storageId = storageId;
		this.path = path;
	}

	@Override
	public void validate() {
		Validated.validate(storageId != null && !storageId.isEmpty(), "Anvil store action requires a storageId");
		Validated.validate(path != null && !path.isEmpty(), "Anvil store action requires a path");
	}

	@Override
	public void accept(@NotNull ActionInventoryGui gui) {
		var player = gui.getPlayer();
		if (!(gui instanceof AnvilInputGui a)) {
			player.sendMessage(Text.of("Can't execute AnvilStore action on non-anvil screen."));
			return;
		}
		
		var placeHolderContext = PlaceholderContext.of(player);
		
		var parsedId = Placeholders.parseText(Text.of(storageId), placeHolderContext).getString();
		var id = Identifier.tryParse(parsedId);
		if (id == null) {
			player.sendMessage(MessageHelper.toError("Failed to create valid path after parsing placeholders: "+parsedId));
			return;
		}
		
		NbtPath nbtPath;
		try {
			nbtPath = NbtPathArgumentType.nbtPath().parse(new StringReader(Placeholders.parseText(Text.of(path), placeHolderContext).getString()));
		} catch (CommandSyntaxException e) {
			player.sendMessage(MessageHelper.toError("Failed to read NbtPath"));
			e.printStackTrace();
			return;
		}
				
		var commandStorage = player.server.getDataCommandStorage();
		var nbtCompound = commandStorage.get(id);
		try {
			nbtPath.put(nbtCompound, NbtString.of(a.getInput()));
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		commandStorage.set(id, nbtCompound);
	}

	@Override
	public BasicAction copy() {
		return new AnvilStoreAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), storageId, path);
	}
	
	public String getStorageId() {
		return storageId;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
}
