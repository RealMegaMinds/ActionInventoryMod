package megaminds.actioninventory.actions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.pb4.sgui.api.ClickType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.misc.LevelSetter;
import megaminds.actioninventory.util.annotations.Exclude;
import megaminds.actioninventory.util.annotations.Poly;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.screen.slot.SlotActionType;

@NoArgsConstructor
@Getter
@Setter
@Poly("Require")
public final class RequirementAction extends GroupAction {	
	private String entitySelector;

	@Exclude private EntitySelector selector;

	public RequirementAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, String requiredGuiName, BasicAction[] actions, String entitySelector) {
		super(requiredIndex, clicktype, actionType, requiredGuiName, actions);
		this.entitySelector = entitySelector;
	}
	
	public RequirementAction(BasicAction[] actions, String entitySelector) {
		super(actions);
		this.entitySelector = entitySelector;
	}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		if (selector==null || matches(gui.getPlayer())) {
			super.internalClick(index, type, action, gui);
		}
	}
	
	private boolean matches(Entity e) {
		try {
			return e.equals(selector.getEntity(((LevelSetter)e.getCommandSource()).withHigherLevel(2)));
		} catch (CommandSyntaxException e1) {
			return false;
		}
	}

	private void validateSelector() {
		if (entitySelector==null || entitySelector.isBlank()) return;
		
		String whole = "@s"+entitySelector.strip();
		
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
	}
}