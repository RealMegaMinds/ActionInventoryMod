package megaminds.actioninventory.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import eu.pb4.sgui.api.elements.AnimatedGuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import lombok.Setter;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.gui.elements.SlotElement;
import megaminds.actioninventory.util.ElementHelper;
import megaminds.actioninventory.util.GuiHelper;
import megaminds.actioninventory.util.annotations.Exclude;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BuilderGui extends BetterGui {
	private static final Text UNSET = new LiteralText("Not Set");
	
	private ScreenHandlerType<?> type;
	private Identifier name;
	private Text title;
	private boolean includePlayer;
	private boolean lockPlayerInventory;
	private SlotElement[] elements;
	private BasicAction openAction;
	private BasicAction closeAction;
	private BasicAction anyClickAction;
	private BasicAction recipeAction;
	private boolean chained;
	private int size;
	
	public BuilderGui(ServerPlayerEntity player, boolean includePlayerInventorySlots) {
		super(ScreenHandlerType.GENERIC_9X6, player, false);

		setChained(true);
		setTitle(new LiteralText("ActionInventory Builder"));

		setSlot(0, typeEl());
	}

	private GuiElementInterface typeEl() {
		Text name = new LiteralText("Type");
		var lore = List.of(UNSET);
		var typeMap = new HashMap<ScreenHandlerType<?>, Item>();
		typeMap.put(ScreenHandlerType.ANVIL, Items.ANVIL);
		typeMap.put(ScreenHandlerType.BEACON, Items.BEACON);
		typeMap.put(ScreenHandlerType.BLAST_FURNACE, Items.BLAST_FURNACE);
		typeMap.put(ScreenHandlerType.BREWING_STAND, Items.BREWING_STAND);
		typeMap.put(ScreenHandlerType.CARTOGRAPHY_TABLE, Items.CARTOGRAPHY_TABLE);
		typeMap.put(ScreenHandlerType.CRAFTING, Items.CRAFTING_TABLE);
		typeMap.put(ScreenHandlerType.ENCHANTMENT, Items.ENCHANTING_TABLE);
		typeMap.put(ScreenHandlerType.FURNACE, Items.FURNACE);
		typeMap.put(ScreenHandlerType.GENERIC_3X3, Items.DISPENSER);
		typeMap.put(ScreenHandlerType.GENERIC_9X1, Items.CHEST);
		typeMap.put(ScreenHandlerType.GENERIC_9X2, Items.CHEST);
		typeMap.put(ScreenHandlerType.GENERIC_9X3, Items.CHEST);
		typeMap.put(ScreenHandlerType.GENERIC_9X4, Items.CHEST);
		typeMap.put(ScreenHandlerType.GENERIC_9X5, Items.CHEST);
		typeMap.put(ScreenHandlerType.GENERIC_9X6, Items.CHEST);
		typeMap.put(ScreenHandlerType.GRINDSTONE, Items.GRINDSTONE);
		typeMap.put(ScreenHandlerType.HOPPER, Items.HOPPER);
		typeMap.put(ScreenHandlerType.LECTERN, Items.LECTERN);
		typeMap.put(ScreenHandlerType.LOOM, Items.LOOM);
		typeMap.put(ScreenHandlerType.MERCHANT, Items.BELL);
		typeMap.put(ScreenHandlerType.SHULKER_BOX, Items.SHULKER_BOX);
		typeMap.put(ScreenHandlerType.SMITHING, Items.SMITHING_TABLE);
		typeMap.put(ScreenHandlerType.SMOKER, Items.SMOKER);
		typeMap.put(ScreenHandlerType.STONECUTTER, Items.STONECUTTER);
		Registry.SCREEN_HANDLER.forEach(h->typeMap.putIfAbsent(h, Items.GREEN_WOOL));
		ClickCallback callback = GuiHelper.getOptionsI(new LiteralText("Choose A Type"), typeMap.keySet(), h->ElementHelper.stackOf(typeMap.get(h), Registry.SCREEN_HANDLER.getId(h).toString()), h->{
			type=h;
			
			setSlot(0, finished(typeMap.get(h), "Type", callback));
		});
		return new AnimatedGuiElementBuilder()
				.setCallback(callback)
				.setItem(Items.CHEST)
				.setName(name)
				.setLore(lore)
				.saveItemStack()
				.setItem(Items.FURNACE)
				.setName(name)
				.setLore(lore)
				.saveItemStack()
				.setItem(Items.SHULKER_BOX)
				.setName(name)
				.setLore(lore)
				.saveItemStack()
				.setItem(Items.CRAFTING_TABLE)
				.setName(name)
				.setLore(lore)
				.saveItemStack()
				.build();
	}
	
	private static GuiElementInterface finished(Item item, String name, ClickCallback callback) {
		return ElementHelper.of(item, name, 1, true, callback);
	}

	public ActionInventoryBuilder build() {
		return null;
	}
}