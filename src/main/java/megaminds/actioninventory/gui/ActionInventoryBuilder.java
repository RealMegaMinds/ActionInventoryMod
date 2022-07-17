package megaminds.actioninventory.gui;

import java.util.Arrays;
import eu.pb4.sgui.api.GuiHelpers;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.EmptyAction;
import megaminds.actioninventory.gui.elements.SlotElement;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.ValidationException;
import megaminds.actioninventory.util.annotations.Exclude;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Adapted from {@link eu.pb4.sgui.api.gui.SimpleGuiBuilder}.
 */
public final class ActionInventoryBuilder implements Validated {
	private static final SlotElement[] EMPTY = new SlotElement[0];

	private ScreenHandlerType<?> type;
	private Identifier name;
	private Text title;
	private TriState includePlayer = TriState.DEFAULT;
	private TriState lockPlayerInventory = TriState.DEFAULT;

	private SlotElement[] elements;
	/**@since 3.1*/
	private BasicAction openAction;
	/**@since 3.1*/
	private BasicAction closeAction;
	/**@since 3.1*/
	private BasicAction anyClickAction;
	/**@since 3.1*/
	private BasicAction recipeAction;
	/**@since 3.2*/
	private TriState chained = TriState.DEFAULT;

	@Exclude private int size;

	private ActionInventoryBuilder() {}

	@Override
	public void validate() {
		Validated.validate(type!=null, "ActionInventories requires type to be non-null.");
		Validated.validate(name!=null, "ActionInventories requires name to be non-null.");
		if (title==null) title = Text.empty();
		if (openAction==null) openAction = EmptyAction.INSTANCE;
		if (closeAction==null) closeAction = EmptyAction.INSTANCE;
		if (anyClickAction==null) anyClickAction = EmptyAction.INSTANCE;
		if (recipeAction==null) recipeAction = EmptyAction.INSTANCE;

		size = GuiHelpers.getHeight(type)*GuiHelpers.getWidth(type) + (includePlayer.orElse(false) ? 36 : 0);

		if (elements==null) {
			elements = EMPTY; 
			return;
		}
		var len = elements.length;
		Validated.validate(len<=size, ()->"Too many elements. Screen handler type "+Registry.SCREEN_HANDLER.getId(type)+" requires there to be a maximum of "+size+" SlotElements");
		var test = new boolean[size];
		for (var e : elements) {
			if (e!=null) {
				var i = e.getIndex();
				Validated.validate(i<size, ()->"Screen handler type "+Registry.SCREEN_HANDLER.getId(type)+" requires SlotElements to have an index below "+size);
				if (i>=0) {
					Validated.validate(!test[i], "A slot has declared an already used index: "+i);
					test[i] = true;
				}
			}
		}
	}

	public ActionInventoryBuilder(ScreenHandlerType<?> type, Identifier name, TriState includePlayerInventorySlots) {
		this.type = type;
		this.name = name;
		this.includePlayer = includePlayerInventorySlots;
		this.lockPlayerInventory = includePlayerInventorySlots;

		this.title = Text.empty();
		this.closeAction = EmptyAction.INSTANCE;
		this.openAction = EmptyAction.INSTANCE;
		this.anyClickAction = EmptyAction.INSTANCE;

		this.size = GuiHelpers.getHeight(type)*GuiHelpers.getWidth(type) + (includePlayer.orElse(false) ? 36 : 0);
		this.elements = new SlotElement[this.size];
	}

	/**
	 * {@link #validate()} is called when using this constructor
	 */
	public ActionInventoryBuilder(ScreenHandlerType<?> type, Identifier name, Text title, TriState includePlayerInventorySlots, SlotElement[] elements, BasicAction openAction, BasicAction closeAction, BasicAction anyClickAction) throws ValidationException {
		this.type = type;
		this.name = name;
		this.title = title;
		this.includePlayer = includePlayerInventorySlots;
		this.lockPlayerInventory = includePlayerInventorySlots;
		this.elements = elements;
		this.closeAction = closeAction;
		this.openAction = openAction;
		this.anyClickAction = anyClickAction;

		this.size = GuiHelpers.getHeight(type)*GuiHelpers.getWidth(type) + (includePlayer.orElse(false) ? 36 : 0);

		validate();
	}

	public ActionInventoryGui build(ServerPlayerEntity player) {
		var gui = new ActionInventoryGui(type, player, includePlayer.orElse(false), name, openAction, closeAction, anyClickAction, recipeAction);
		gui.setTitle(title);
		gui.setLockPlayerInventory(lockPlayerInventory.orElse(false));
		gui.setChained(chained.orElse(false));

		for (var element : elements) {
			if (element != null) {
				element.apply(gui, player);
			}
		}

		return gui;
	}

	public void setLockPlayerInventory(TriState lockPlayerInventory) {
		if (!includePlayer.orElse(false)) this.lockPlayerInventory = lockPlayerInventory;
	}

	public ActionInventoryBuilder copy() {
		var builder = new ActionInventoryBuilder();
		builder.type = type;
		builder.name = name;
		builder.title = title.copy();
		builder.includePlayer = includePlayer;
		builder.lockPlayerInventory = lockPlayerInventory;
		builder.elements = Arrays.stream(elements).map(SlotElement::copy).toArray(SlotElement[]::new);
		builder.anyClickAction = anyClickAction;
		builder.closeAction = closeAction;
		builder.openAction = openAction;
		builder.recipeAction = recipeAction;

		builder.size = size;
		return builder;
	}

	public ScreenHandlerType<?> getType() {
		return type;
	}

	public Identifier getName() {
		return name;
	}

	public Text getTitle() {
		return title;
	}

	public TriState getIncludePlayer() {
		return includePlayer;
	}

	public TriState getLockPlayerInventory() {
		return lockPlayerInventory;
	}

	public SlotElement[] getElements() {
		return elements;
	}

	public BasicAction getOpenAction() {
		return openAction;
	}

	public BasicAction getCloseAction() {
		return closeAction;
	}

	public BasicAction getAnyClickAction() {
		return anyClickAction;
	}

	public BasicAction getRecipeAction() {
		return recipeAction;
	}

	public TriState getChained() {
		return chained;
	}

	public int getSize() {
		return size;
	}

	public void setTitle(Text title) {
		this.title = title;
	}

	public void setElements(SlotElement[] elements) {
		this.elements = elements;
	}

	public void setOpenAction(BasicAction openAction) {
		this.openAction = openAction;
	}

	public void setCloseAction(BasicAction closeAction) {
		this.closeAction = closeAction;
	}

	public void setAnyClickAction(BasicAction anyClickAction) {
		this.anyClickAction = anyClickAction;
	}

	public void setRecipeAction(BasicAction recipeAction) {
		this.recipeAction = recipeAction;
	}

	public void setChained(TriState chained) {
		this.chained = chained;
	}
}