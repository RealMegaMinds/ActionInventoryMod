package megaminds.testmod;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class CommandInventory extends SimpleInventory implements NamedScreenHandlerFactory {
	private static final Map<ItemStack, CommandInventory> commandInventories = new HashMap<>();

	private final Text name;
	
	public CommandInventory(Text name, ItemStack opener) {
		super(54);
		this.name = name;
		commandInventories.put(opener, this);
	}

	public CommandInventory(Text name, ItemStack opener, ItemStack... items) {
		super(54);
		this.name = name;
		for (ItemStack stack : items) {
			addStack(stack);
		}
		commandInventories.put(opener, this);
	}
	
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this);
	}

	@Override
	public Text getDisplayName() {
		return name;
	}
	
	/**
	 * Gets the {@link CommandInventory} linked to the given {@link ItemStack}. <br>
	 * Note: {@link CommandInventory}s are linked to an {@link ItemStack} when constructed.
	 * @param item
	 * The {@link ItemStack} to check.
	 * @return
	 * The {@link CommandInventory} linked to the given {@link ItemStack}. If a {@link CommandInventory} cannot be found, returns null.
	 */
	public static CommandInventory get(ItemStack item) {
		for (Entry<ItemStack, CommandInventory> e : commandInventories.entrySet()) {
			if (ItemStack.areEqual(e.getKey(), item)) {
				return e.getValue();
			}
		}
		return null;
	}
}