package megaminds.actioninventory.actions;

import java.util.Arrays;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.pb4.sgui.api.ClickType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.util.annotations.Exclude;
import megaminds.actioninventory.util.annotations.PolyName;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@NoArgsConstructor
@PolyName("Require")
public final class RequirementAction extends GroupAction {	
	@Getter @Setter private String entitySelector;
	@Getter @Setter private EntityPredicate entityPredicate;

	@Exclude private EntitySelector selector;

	public RequirementAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, TriState requireShift, Identifier requiredRecipe,  Identifier requiredGuiName, ClickAwareAction[] actions, String entitySelector, EntityPredicate entityPredicate) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName, actions);
		this.entitySelector = entitySelector;
		this.entityPredicate = entityPredicate;
	}

	public RequirementAction(ClickAwareAction[] actions, String entitySelector, EntityPredicate entityPredicate) {
		super(actions);
		this.entitySelector = entitySelector;
		this.entityPredicate = entityPredicate;
	}

	@Override
	public void accept(ActionInventoryGui gui) {
		var p = gui.getPlayer();
		if (selector==null || entityPredicate.test(p, p) && matches(p)) {
			super.accept(gui);
		}
	}

	private boolean matches(Entity e) {
		try {
			return e.equals(selector.getEntity(e.getCommandSource().withMaxLevel(2)));
		} catch (CommandSyntaxException e1) {
			return false;
		}
	}

	private void validateSelector() {
		if (entitySelector==null || entitySelector.isBlank()) return;

		var whole = "@s"+entitySelector.strip();

		try {
			this.selector = new EntitySelectorReader(new StringReader(whole)).read();
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException("Failed to read entity selector for an EntityOpener.", e);
		}
	}

	@Override
	public void validate() {
		super.validate();
		validateSelector();
		if (entityPredicate==null) entityPredicate = EntityPredicate.ANY;
	}

	@Override
	public ClickAwareAction copy() {
		var copy = new RequirementAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName(), Arrays.stream(getActions()).map(ClickAwareAction::copy).toArray(ClickAwareAction[]::new), entitySelector, entityPredicate);
		copy.selector = selector;
		return copy;
	}
}