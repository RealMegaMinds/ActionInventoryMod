package megaminds.actioninventory.actions;

import java.util.Objects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.LevelSetter;
import megaminds.actioninventory.gui.NamedGui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.TypeName;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.screen.slot.SlotActionType;

@TypeName("Require")
public final class RequirementAction extends BasicAction {	
	private BasicAction[] actions;
	//null or empty allows all
	private String entitySelector;
	private transient EntitySelector selector;
	private boolean failed;

	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, NamedSlotGuiInterface gui) {
		if (!failed && selector==null) {
			fixSelector();
		}
		
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

	public void fixSelector() {
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