package megaminds.actioninventory.gui;

import java.util.function.Function;

import com.google.gson.annotations.JsonAdapter;

import megaminds.actioninventory.serialization.SlotFunctionSerializer;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

@JsonAdapter(SlotFunctionSerializer.class)
public interface SlotFunction extends Function<ServerPlayerEntity, Slot> {
	public enum InventoryType {PLAYER, ENDERCHEST, GENERATED}
	
	default InventoryType getType() {
		return InventoryType.GENERATED;
	}
	default String getName() {
		return "This cannot be deserialized.";
	}
	default int getRedirectIndex() {
		return apply(null).getIndex();
	}
}