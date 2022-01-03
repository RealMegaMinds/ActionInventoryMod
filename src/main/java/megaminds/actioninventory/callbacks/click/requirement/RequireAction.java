package megaminds.actioninventory.callbacks.click.requirement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.MustBeInvokedByOverriders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import megaminds.actioninventory.callbacks.click.BasicAction;
import net.minecraft.screen.slot.SlotActionType;

public abstract class RequireAction extends BasicAction {
	private static final Map<UUID, RequirementStore> STORED_REQUIREMENTS = new HashMap<>();
	private static final String ACTIIONS = "actions", CONSUMES = "consumes", RESETS = "resets";
	
	private BasicAction[] actions;
	private boolean consumes, resets;
	
	@Override
	public void internalClick(int index, ClickType type, SlotActionType action, SlotGuiInterface gui) {
		if (shouldExecuteActions(index, type, action, gui)) {
			for (BasicAction a : actions) {
				a.internalClick(index, type, action, gui);
			}
			if (consumes) consume(index, type, action, gui, !resets);
		}
	}
	
	protected RequirementStore getStore(UUID uuid) {
		return STORED_REQUIREMENTS.computeIfAbsent(uuid, RequirementStore::new);
	}
	
	/**
	 * Performs a check. Should retrieve the RequirementStore and check if player has already paid (if applicable).
	 */
	protected abstract boolean shouldExecuteActions(int index, ClickType type, SlotActionType action, SlotGuiInterface gui);
	/**
	 * Actually takes stuff from the player (only called if consumes is true).
	 */
	protected abstract void consume(int index, ClickType type, SlotActionType action, SlotGuiInterface gui, boolean logToStore);
	
	@Override
	@MustBeInvokedByOverriders
	public BasicAction fromJson(JsonObject obj, JsonDeserializationContext context) {
		actions = obj.has(ACTIIONS) ? context.deserialize(obj.get(ACTIIONS), BasicAction[].class) : new BasicAction[0];
		consumes = obj.has(CONSUMES) ? obj.get(CONSUMES).getAsBoolean() : false;
		resets = obj.has(RESETS) ? obj.get(RESETS).getAsBoolean() : false;
		return this;
	}
}