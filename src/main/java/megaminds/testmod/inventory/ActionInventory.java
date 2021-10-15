package megaminds.testmod.inventory;

import java.util.List;

import com.google.common.collect.ImmutableList;

import megaminds.testmod.inventory.ActionInventoryManager.OpenType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

//might need to be by player
public abstract class ActionInventory implements Inventory, NamedScreenHandlerFactory {
	private final String name;
	private final int rows;
	private final OpenRequirement openRequirement;
	private final ImmutableList<InventoryItem> items;
	
	private ActionInventory(String name, List<OpenType> acceptedTypes, int rows, List<InventoryItem> items) {
		this.name = name;
		this.rows = rows;
		this.acceptedTypes = ImmutableList.copyOf(acceptedTypes);
		this.items = ImmutableList.copyOf(items);
	}
	
	public static ActionInventory getNewInventory(String name) {
		if (ActionInventoryManager.getInventory(name)!=null) {
			return null;//new ActionInventory(name);
		}
		return null;
	}
	
	public OpenRequirement getOpenRequirement() {
		return openRequirement;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract boolean shouldOpen(ServerPlayerEntity player, BlockState state, BlockEntity entity, Direction dir);
	
	public abstract boolean shouldOpen(ServerPlayerEntity player, ItemStack stack);

	public abstract boolean onClicked(ServerPlayerEntity player, int slot);
	
	@Override
	public void onOpen(PlayerEntity player) {
		if (player.world.isClient) return;
		ActionInventoryManager.onOpen((ServerPlayerEntity) player, this);
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (player.world.isClient) return;
		ActionInventoryManager.onClose((ServerPlayerEntity) player, this);
	}
	
	//many things can be defaulted
}