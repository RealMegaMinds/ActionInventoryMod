package megaminds.actioninventory.gui;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.GuiHelpers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import megaminds.actioninventory.actions.ClickAwareAction;
import megaminds.actioninventory.actions.EmptyAction;
import megaminds.actioninventory.gui.elements.SlotElement;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Exclude;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Creators of {@link ActionInventoryBuilder}s are responsible for calling {@link #validate(Consumer)} to ensure correctness.
 */
@Getter
@Setter
public final class ActionInventoryBuilder implements Validated {
	@Setter(AccessLevel.NONE) private ScreenHandlerType<?> type;//required
	@Setter(AccessLevel.NONE) private Identifier name;//required

	private Text title = LiteralText.EMPTY;
	private TriState includePlayer = TriState.DEFAULT;
	private TriState lockPlayerInventory = TriState.DEFAULT;
	/**@since 3.2*/
	private TriState chained = TriState.DEFAULT;
	private List<SlotElement> elements = Collections.emptyList();
	/**@since 3.1*/
	private ClickAwareAction openAction = EmptyAction.INSTANCE;
	/**@since 3.1*/
	private ClickAwareAction closeAction = EmptyAction.INSTANCE;
	/**@since 3.1*/
	private ClickAwareAction anyClickAction = EmptyAction.INSTANCE;
	/**@since 3.1*/
	private ClickAwareAction recipeAction = EmptyAction.INSTANCE;

	@Setter(AccessLevel.NONE) @Exclude private int size;

	public ActionInventoryBuilder() {}

	public ActionInventoryBuilder(ScreenHandlerType<?> type, Identifier name) {
		this.type = type;
		this.name = name;
	}

	public ActionInventoryBuilder(ScreenHandlerType<?> type, Identifier name, Text title, TriState includePlayerInventorySlots, TriState lockPlayerInventory, TriState chained, List<SlotElement> elements, ClickAwareAction openAction, ClickAwareAction closeAction, ClickAwareAction anyClickAction, ClickAwareAction recipeAction) {	//NOSONAR
		this.type = type;
		this.name = name;
		this.title = title;
		this.includePlayer = includePlayerInventorySlots;
		this.lockPlayerInventory = lockPlayerInventory;
		this.chained = chained;
		this.elements = elements;
		this.openAction = openAction;
		this.closeAction = closeAction;
		this.anyClickAction = anyClickAction;
		this.recipeAction = recipeAction;
	}

	@Override
	public void validate(@NotNull Consumer<String> errorReporter) {
		if (type==null) errorReporter.accept("ActionInventories requires type to be non-null. Elements will not be checked.");
		if (name==null) errorReporter.accept("ActionInventories requires name to be non-null.");

		if (title==null) title = LiteralText.EMPTY;
		//TextSerializer should've already validated title

		if (openAction==null) openAction = EmptyAction.INSTANCE;
		openAction.validate(errorReporter);

		if (closeAction==null) closeAction = EmptyAction.INSTANCE;
		closeAction.validate(errorReporter);

		if (anyClickAction==null) anyClickAction = EmptyAction.INSTANCE;
		anyClickAction.validate(errorReporter);

		if (recipeAction==null) recipeAction = EmptyAction.INSTANCE;
		recipeAction.validate(errorReporter);

		if (type==null) return;	//type is required for further error checking

		size = GuiHelpers.getHeight(type)*GuiHelpers.getWidth(type) + (includePlayer.orElse(false) ? 36 : 0);

		if (elements==null) {
			elements = Collections.emptyList();	//no further error checking needed
			return;
		}

		validateElements(errorReporter);
	}

	private void validateElements(Consumer<String> errorReporter) {
		elements.removeIf(Objects::isNull);
		var len = elements.size();
		if (len>size) errorReporter.accept("Elements size is: "+len+", but type: "+Registry.SCREEN_HANDLER.getId(type)+" only supports a maximum size of: "+size);

		var test = new boolean[size];
		for (var e : elements) {
			e.validate(errorReporter);
			var i = e.getIndex();
			if (i>=size) errorReporter.accept("Element index is: "+i+", but type: "+Registry.SCREEN_HANDLER.getId(type)+" needs index to be below: "+size);
			if (i>=0) {
				if (test[i]) errorReporter.accept("Multiple elements have declared the same index: "+i);
				test[i] = true;
			}
		}
	}

	public ActionInventoryGui build(ServerPlayerEntity player) {
		var gui = new ActionInventoryGui(player, type, name, title, includePlayer.orElse(false), lockPlayerInventory.orElse(false), chained.orElse(false), openAction, closeAction, anyClickAction, recipeAction);
		elements.forEach(e->e.apply(gui, player));
		return gui;
	}

	public ActionInventoryBuilder copy() {
		var builder = new ActionInventoryBuilder();
		builder.type = type;
		builder.name = name;
		builder.title = title.shallowCopy();
		builder.includePlayer = includePlayer;
		builder.lockPlayerInventory = lockPlayerInventory;
		builder.chained = chained;
		builder.elements = elements.stream().map(SlotElement::copy).toList();
		builder.anyClickAction = anyClickAction;
		builder.closeAction = closeAction;
		builder.openAction = openAction;
		builder.recipeAction = recipeAction;

		builder.size = size;
		return builder;
	}
}