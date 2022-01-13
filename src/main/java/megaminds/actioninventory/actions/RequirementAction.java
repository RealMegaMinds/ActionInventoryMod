package megaminds.actioninventory.actions;

import java.util.Objects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.misc.LevelSetter;
import megaminds.actioninventory.util.annotations.Exclude;
import megaminds.actioninventory.util.annotations.TypeName;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.screen.slot.SlotActionType;

@TypeName("Require")
public final class RequirementAction extends BasicAction {	
	private BasicAction[] actions;
	private String entitySelector;
	private boolean failed;

	@Exclude private EntitySelector selector;
	
	private RequirementAction() {}

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		if (!failed && selector==null) {
			fixSelector();
		}
		
		if (actions==null) actions = new BasicAction[0];
		if (selector==null || matches(gui.getPlayer())) {
			for (BasicAction a : actions) {
				a.internalClick(index, type, action, gui);
			}
		}
	}
	
	private boolean matches(Entity e) {
		try {
			return e.equals(selector.getEntity(((LevelSetter)e.getCommandSource()).withHigherLevel(2)));
		} catch (CommandSyntaxException e1) {
			return false;
		}
	}

	private void fixSelector() {
		String whole = "@s"+Objects.requireNonNullElse(entitySelector, "").strip();
		
		try {
			this.selector = new EntitySelectorReader(new StringReader(whole)).read();
		} catch (CommandSyntaxException e) {
			ActionInventoryMod.warn("Failed to read entity selector for an EntityOpener.");
			e.printStackTrace();
			this.failed = true;
			this.selector = null;
		}
	}
}