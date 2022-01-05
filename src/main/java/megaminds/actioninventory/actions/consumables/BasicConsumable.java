package megaminds.actioninventory.actions.consumables;

import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class BasicConsumable {
	private static final String REQUIRE_FULL = "requireFullAmount";
	private boolean requireFull;
	//TODO make serializer
	public abstract boolean canConsume(ServerPlayerEntity player);
	public abstract NbtElement consume(ServerPlayerEntity player, NbtElement storage);
	public abstract Consumable getType();
	public void requireFull() {
		requireFull = true;
	}
	
	//TODO extract to own class and fill
	public enum Consumable{}
	
	//TODO tojson and fromJson
}