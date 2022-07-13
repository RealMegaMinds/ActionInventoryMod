package megaminds.actioninventory.actions;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.ActionInventoryGui;
import megaminds.actioninventory.gui.callback.ActionInventoryCallback;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.annotations.Exclude;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public abstract class Action implements Consumer<ActionInventoryGui>, Validated, ActionInventoryCallback {
	@Exclude(deserialize = false)
	private final Identifier type = getType();

	/**
	 * Returns the type of this action. Used for deserialization.
	 */
	public abstract Identifier getType();

	/**
	 * Does this action for the given ActionInventory.
	 */
	@Override
	public abstract void accept(@NotNull ActionInventoryGui gui);
	@Override
	public abstract void onRecipe(Identifier recipe, boolean shift, ActionInventoryGui gui);

	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, ActionInventoryGui gui) {
		accept(gui);
		return false;
	}
}